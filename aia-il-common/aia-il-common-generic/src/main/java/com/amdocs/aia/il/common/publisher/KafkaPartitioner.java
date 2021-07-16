package com.amdocs.aia.il.common.publisher;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class KafkaPartitioner implements Partitioner {
    public static final String PARTITION_KEY_SEPARATOR = "-";

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        Integer partitionQuantity = cluster.partitionCountForTopic(topic);
        return partition(key, partitionQuantity);
    }

    public int partition(Object key, Integer partitionQuantity) {
       return stringToPartition((String)key, partitionQuantity);
    }

    @Override
    public void close() {
        // Do Nothing
    }

    @Override
    public void configure(Map<String, ?> map) {
        // Do Nothing
    }

    private int stringToPartition(String value, int partitionQuantity){
        if (partitionQuantity == 1){
            return 0;
        }
        int hash = value.hashCode();
        return Math.abs(hash % partitionQuantity);
    }
}
