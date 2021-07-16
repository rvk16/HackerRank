package com.amdocs.aia.il.common.publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerPartitionAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The LagBasedPartitionAssignor operates on a per-topic basis, and attempts to assign partitions such that lag is
 * distributed evenly across a consumer group.
 *
 * For each topic, we first obtain the lag on all partitions.  Lag on a given partition is the difference between the
 * end offset and the last offset committed by the consumer group.  If no offsets have been committed for a partition we
 * determine the lag based on the {@code auto.offset.reset} property.  If {@code auto.offset.reset=latest}, we assign a
 * lag of 0.  If {@code auto.offset.reset=earliest} (or any other value) we assume assign lag equal to the total number
 * of message currently available in that partition.
 *
 * We then create a map storing the current total lag of all partitions assigned to each member of the consumer group.
 * Partitions are assigned in decreasing order of lag, with each partition assigned to the consumer with least total
 * number of assigned partitions, breaking ties by assigning to the consumer with the least total assigned lag.
 *
 * Distributing partitions as evenly across consumers (by count) ensures that the partition assignment is balanced when
 * all partitions have a current lag of 0 or if the distribution of lags is heavily skewed.  It also gives the consumer
 * group the best possible chance of remaining balanced if the assignment is retained for a long period.
 *
 * For example, suppose there are two consumers C0 and C1, both subscribed to a topic t0 having 3 partitions with the
 * following lags:
 * t0p0: 100,000
 * t0p1:  50,000
 * t0p2:  60,000
 *
 * The assignment will be:
 * C0: [t0p0]
 * C1: [t0p1, t0p2]
 *
 * The total lag or partitions assigned to each consumer will be:
 * C0: 100,000
 * C1: 110,000
 *
 * Compare this to the assignments made by the {@link org.apache.kafka.clients.consumer.RangeAssignor}:
 * C0: [t0p0, t0p1]
 * C1: [t0p2]
 *
 * The RangeAssignor results in a less balanced total lag for each consumer of:
 * C0: 160,000
 * C1:  50,000
 *
 * @see ConsumerPartitionAssignor
 * @see org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor
 * @see org.apache.kafka.clients.consumer.RangeAssignor
 */
public class LagBasedPartitionAssignor implements ConsumerPartitionAssignor, Configurable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LagBasedPartitionAssignor.class);

    private Properties consumerGroupProps;
    private Properties metadataConsumerProps;
    private KafkaConsumer<byte[], byte[]> metadataConsumer;

    /**
     * This class is instantiated by reflection, at which point Kafka passes in the consumer config properties for
     * the current instance via this method.
     *
     * @param configs Kafka consumer configuration properties for the current instance
     */
    @Override
    public void configure(Map<String, ?> configs) {

        // Construct Properties from config map
        consumerGroupProps = new Properties();
        for (final Map.Entry<String, ?> prop : configs.entrySet()) {
            consumerGroupProps.put(prop.getKey(), prop.getValue());
        }

        // group.id must be defined
        final String groupId = consumerGroupProps.getProperty(ConsumerConfig.GROUP_ID_CONFIG);
        if (groupId == null) {
            throw new IllegalArgumentException(
                    ConsumerConfig.GROUP_ID_CONFIG + " cannot be null when using "
                            + ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG + "="
                            + this.getClass().getName());
        }

        // Create a new consumer that can be used to get lag metadata for the consumer group
        metadataConsumerProps = new Properties();
        metadataConsumerProps.putAll(consumerGroupProps);
        metadataConsumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        final String clientId = groupId + ".assignor";
        metadataConsumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);

        LOGGER.debug(
                "Configured LagBasedPartitionAssignor with values:\n"
                        + "\tgroup.id = {}\n"
                        + "\tclient.id = {}\n",
                groupId,
                clientId
        );

    }

    @Override
    public String name() {
        return "lag";
    }

    @Override
    public GroupAssignment assign(Cluster metadata, GroupSubscription subscriptions) {

        final Set<String> allSubscribedTopics = new HashSet<>();
        final Map<String, List<String>> topicSubscriptions = new HashMap<>();
        for (Map.Entry<String, Subscription> subscriptionEntry : subscriptions.groupSubscription().entrySet()) {
            List<String> topics = subscriptionEntry.getValue().topics();
            allSubscribedTopics.addAll(topics);
            topicSubscriptions.put(subscriptionEntry.getKey(), topics);
        }

        final Map<String, List<TopicPartitionLag>> topicLags = readTopicPartitionLags(metadata, allSubscribedTopics);
        Map<String, List<TopicPartition>> rawAssignments = assign(topicLags, topicSubscriptions);

        // this class has maintains no user data, so just wrap the results
        Map<String, Assignment> assignments = new HashMap<>();
        for (Map.Entry<String, List<TopicPartition>> assignmentEntry : rawAssignments.entrySet()) {
            assignments.put(assignmentEntry.getKey(), new Assignment(assignmentEntry.getValue()));
        }
        return new GroupAssignment(assignments);
    }

    /**
     * Perform the group assignment based on the lag on each partition for the current consumer group.
     *
     * @param partitionLagPerTopic Map from topic to a list of the lag on each partition for the current consumer group
     * @param subscriptions      Map from the memberId to their respective topic subscription
     * @return Map from each member to the list of partitions assigned to them.
     */
    static Map<String, List<TopicPartition>> assign(
            Map<String, List<TopicPartitionLag>> partitionLagPerTopic,
            Map<String, List<String>> subscriptions
    ) {

        final Map<String, List<TopicPartition>> assignment = new HashMap<>();
        // Track total lag assigned to each consumer (for the current topic)
        final Map<String, Long> consumerTotalLags = new HashMap<>();
        // Track total number of partitions assigned to each consumer (for the current topic)
        final Map<String, Integer> consumerTotalPartitions = new HashMap<>();
        for (String memberId : subscriptions.keySet()) {
            assignment.put(memberId, new ArrayList<>());
            consumerTotalLags.put(memberId, 0L);
            consumerTotalPartitions.put(memberId, 0);
        }
        List<TopicPartitionLag> lagsCombined = new ArrayList<>();
        partitionLagPerTopic.values().forEach(lagsCombined::addAll);

        assignTopicPartition(
                assignment,
                consumerTotalLags.keySet(),
                lagsCombined,
                consumerTotalLags,
                consumerTotalPartitions
        );
        return assignment;
    }

    private static void assignTopicPartition(
            final Map<String, List<TopicPartition>> assignment,
            final Set<String> consumers,
            final List<TopicPartitionLag> partitionLags,
            final Map<String, Long> consumerTotalLags,
            final Map<String, Integer> consumerTotalPartitions
    ) {

        if (consumers.isEmpty()) {
            return;
        }

        // Assign partitions in descending order of lag, then ascending by partition
        partitionLags.sort((p1, p2) -> {
            // If lag is equal, lowest partition id first
            if (p1.getLag() == p2.getLag()) {
                return Integer.compare(p1.getPartition(), p2.getPartition());
            }
            // Highest lag first
            return Long.compare(p2.getLag(), p1.getLag());
        });

        for (TopicPartitionLag partition : partitionLags) {

            // Assign to the consumer with least number of partitions, then smallest total lag, then smallest id
            final String memberId = Collections
                    .min(
                            consumers,
                            (c1, c2) -> {

                                // Lowest partition count first
                                final int comparePartitionCount = Integer.compare(consumerTotalPartitions.get(c1),
                                        consumerTotalPartitions.get(c2));
                                if (comparePartitionCount != 0) {
                                    return comparePartitionCount;
                                }
                                // If partition count is equal, lowest total lag first
                                final int compareTotalLags = Long.compare(consumerTotalLags.get(c1), consumerTotalLags.get(c2));
                                if (compareTotalLags != 0) {
                                    return compareTotalLags;
                                }
                                // If total lag is equal, lowest consumer id first
                                return c1.compareTo(c2);

                            }
                    );
            assignment.get(memberId).add(new TopicPartition(partition.getTopic(), partition.getPartition()));
            consumerTotalLags.put(memberId, consumerTotalLags.getOrDefault(memberId, 0L) + partition.getLag());
            consumerTotalPartitions.put(memberId, consumerTotalPartitions.getOrDefault(memberId, 0) + 1);

            LOGGER.trace(
                    "Assigned partition {}-{} to consumer {}.  partition_lag={}, consumer_current_total_lag={}",
                    partition.getTopic(),
                    partition.getPartition(),
                    memberId,
                    partition.getLag(),
                    consumerTotalLags.get(memberId)
            );

        }

        // Log assignment and total consumer lags for current topic
        if (LOGGER.isDebugEnabled()) {

            final StringBuilder topicSummary = new StringBuilder();
            for (Map.Entry<String, Long> entry : consumerTotalLags.entrySet()) {

                final String memberId = entry.getKey();
                topicSummary.append(
                        String.format(
                                "\t%s (total_lag=%d)%n",
                                memberId,
                                consumerTotalLags.get(memberId)
                        )
                );

                for (TopicPartition tp : assignment.getOrDefault(memberId, Collections.emptyList())) {
                    topicSummary.append(String.format("\t\t%s%n", tp));
                }

            }

        }

    }

    /**
     * Lookup the current consumer group lag for each partition
     *
     * @param metadata            cluster metadata
     * @param allSubscribedTopics a list of all topics subscribed to by at least one member of the consumer group
     * @return map from topic to the lag for each partition. Topics not in metadata will be excluded from this map.
     */
    private Map<String, List<TopicPartitionLag>> readTopicPartitionLags(
            final Cluster metadata,
            final Set<String> allSubscribedTopics
    ) {

        if (metadataConsumer == null) {
            metadataConsumer = new KafkaConsumer<>(metadataConsumerProps);
        }

        final Map<String, List<TopicPartitionLag>> topicPartitionLags = new HashMap<>();
        for (String topic : allSubscribedTopics) {

            final List<PartitionInfo> topicPartitionInfo = metadata.partitionsForTopic(topic);
            if (topicPartitionInfo != null && !topicPartitionInfo.isEmpty()) {

                final List<TopicPartition> topicPartitions = topicPartitionInfo.stream().map(
                        (PartitionInfo p) -> new TopicPartition(p.topic(), p.partition())
                ).collect(Collectors.toList());

                topicPartitionLags.put(topic, new ArrayList<>());

                // Get begin/end offset in each partition
                final Map<TopicPartition, Long> topicBeginOffsets = metadataConsumer.beginningOffsets(topicPartitions);
                final Map<TopicPartition, Long> topicEndOffsets = metadataConsumer.endOffsets(topicPartitions);

                Map<TopicPartition, OffsetAndMetadata> partitionMetadata = metadataConsumer.committed(new HashSet<>(topicPartitions));
                // Determine lag for each partition
                for (TopicPartition partition : topicPartitions) {

                    final String autoOffsetResetMode = consumerGroupProps
                            .getProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
                    final long lag = computePartitionLag(
                            partitionMetadata.get(partition),
                            topicBeginOffsets.getOrDefault(partition, 0L),
                            topicEndOffsets.getOrDefault(partition, 0L),
                            autoOffsetResetMode
                    );
                    topicPartitionLags.get(topic).add(new TopicPartitionLag(topic, partition.partition(), lag));

                }

            } else {
                LOGGER.warn("Skipping assignment for topic {} since no metadata is available", topic);
            }
        }

        return topicPartitionLags;

    }

    /**
     * Compute the current lag for a partition
     *
     * @param partitionMetadata   last committed offset for the partition
     * @param beginOffset         earliest available offset in the partition
     * @param endOffset           offset of next message to be appended to the partition
     * @param autoOffsetResetMode offset reset mode (earliest, latest, none)
     * @return the current lag
     */
    static long computePartitionLag(
            final OffsetAndMetadata partitionMetadata,
            final long beginOffset,
            final long endOffset,
            final String autoOffsetResetMode
    ) {

        final long nextOffset;
        if (partitionMetadata != null) {

            nextOffset = partitionMetadata.offset();

        } else {

            // No committed offset for this partition, set based on auto.offset.reset
            if ("latest".equalsIgnoreCase(autoOffsetResetMode)) {
                nextOffset = endOffset;
            } else {
                // assume earliest
                nextOffset = beginOffset;
            }

        }

        // The max() protects against the unlikely case when reading the partition end offset fails
        // but reading the last committed offsets succeeds
        return Long.max(endOffset - nextOffset, 0L);

    }

    /**
     * Extends {@link TopicPartition} to include the lag for current consumer group on each partition
     */
    static class TopicPartitionLag {

        private final String topic;
        private final int partition;
        private final long lag;

        TopicPartitionLag(String topic, int partition, long lag) {
            this.topic = topic;
            this.partition = partition;
            this.lag = lag;
        }

        String getTopic() {
            return topic;
        }

        int getPartition() {
            return partition;
        }

        long getLag() {
            return lag;
        }

    }

}
