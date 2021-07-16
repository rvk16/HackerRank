package com.amdocs.aia.il.common.utils;

import com.amdocs.aia.common.serialization.messages.EnumValue;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.stores.KeyColumn;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public final class PublisherUtils {

    public static final String TABLE_NAME_SEPARATOR = "_";
    public static final String PARTITION_KEY_SEPARATOR = "-";
    public static final String VALUE_DELIMITERS = ",";
    public static final String BLANK = "";

    private static final String FEATURE_PREFIX = "f_";
    private static final String DATA_CHANNEL = "DataChannel";
    public static final String TRANSFORMER = "_transformer";
    public static final String PUBLISHER_STORE = "PublisherStore";

    public static final String TRANSACTION_TIME_FIELD_NAME = "transactionTime";
    public static final String QUERY_TIME_FIELD_NAME = "queryTime";
    public static final String LAST_PROCESSING_TIME = "lastProcessingTime";
    public static final String ARRIVAL_TIME_FIELD_NAME = "arrivalTime";

    public static final String TOPIC = "topic";
    public static final String VALUE = "value";
    public static final String TRANSACTIONID = "transactionId";

    public static final String MAIN_TABLE = "DATA";
    private static final String OPERATION = "operation";

    private static final String CONTEXT = "Context";

    public static final String COORDINATOR_FIELD_NAME = "coordinator";
    public static final String ISLEADER_FIELD_NAME = "isLeader";
    public static final String TRIGGERED_SCHEDULED_REBALANCING_BY_CONSUMER_ID = "Triggered scheduled rebalancing by consumer:{}";
    public static final String SCHEDULED_REBALANCING_NOT_TRIGGERED = "Scheduled rebalancing NOT triggered as consumer:{} is not leader";
    public static final String KAFKA_CONSUMER_PCK = "org.apache.kafka.clients.consumer.KafkaConsumer";
    public static final String CONSUMER_COORDINATOR_PKG = "org.apache.kafka.clients.consumer.internals.ConsumerCoordinator";
    public static final String KAFKA_SSL_TRUSTSTORE_PASSWORD = "KAFKA_SSL_TRUSTSTORE_PASSWORD";//NOSONAR
    public static final String KAFKA_SSL_TRUSTSTORE_PASSWORD_DEPLOYER = "AIA_IL_BUS_DEPLOYER_KAFKA_SSL_TRUSTSTORE_PASSWORD";//NOSONAR

    private PublisherUtils() {
    }

    public static String calculateName(List<ColumnConfiguration> columns) {
        List<String> names = new ArrayList<>();
        columns.forEach(column -> names.add(column.getColumnName()));
        return String.join(TABLE_NAME_SEPARATOR, names);
    }

    public static String getFeatureTableName(String tableName) {
        return FEATURE_PREFIX + tableName.toLowerCase();
    }

    public static String getSourceTableName(String tableName) {
        return tableName.replaceFirst("f_", "").toUpperCase();
    }

    public static String getDataChannelForContextTable(String schemaStoreKey) {
        return schemaStoreKey + DATA_CHANNEL;
    }

    public static String getDataChannelFromIntermidiateTopic(String namespace, String topic) {
        return topic.substring(namespace.length() + 1).replace(TRANSFORMER, "");
    }

    public static String removeNamespace(String namespace, String topic) {
        return topic.replace(namespace + "_", "");
    }



    public static String getPublisherStoreForContextTable(String schemaStoreKey) {
        return schemaStoreKey + PUBLISHER_STORE;
    }

    public static String getSchemaNameWithNamespace(String namespace, String schemaKey) {
        return org.springframework.util.StringUtils.hasText(namespace) ? namespace + TABLE_NAME_SEPARATOR + schemaKey : schemaKey;
    }

    public static String getReplicatorPublisherTopicName(String dataChannelName) {
        return dataChannelName + TRANSFORMER;
    }

    public static String getPublisherStoreForSchemaKey(String schemaStoreKey) {
        return schemaStoreKey.replace(DATA_CHANNEL,PUBLISHER_STORE);
    }

    private static Properties getPropertiesObject(Map<String, String> kafkaProperties) {
        Properties properties = new Properties();
        kafkaProperties.forEach((key, value) ->
                properties.put(key, value));
        return properties;
    }

    public static Producer<String, byte[]> createKafkaProducer(Map<String, String> kafkaProperties) {
        return new KafkaProducer<>(PublisherUtils.getPropertiesObject(kafkaProperties), new StringSerializer(), new ByteArraySerializer());
    }

    public static Producer<String, byte[]> createKafkaProducer(Properties kafkaProperties) {
        return new KafkaProducer<>(kafkaProperties, new StringSerializer(), new ByteArraySerializer());
    }

    public static String buildKey(KeyColumn keyColumn) {
        return StringUtils.join(Arrays.asList(keyColumn.getIds()), PARTITION_KEY_SEPARATOR);
    }

    public static String buildKeyForAudit(String correlationID) {
        return StringUtils.join(Arrays.asList(correlationID), PARTITION_KEY_SEPARATOR);
    }

    public static String getFullName(String prefix, String name, String postFix) {
        return prefix + '_' + name + "_" + postFix;
    }

    public static String getFullName(String prefix, String name) {
        return prefix + '_' + name;
    }

    public static String getContextName(String contextKey) {
        return contextKey.replace(CONTEXT, "");
    }

    public static boolean hasAllFields(RepeatedMessage message, List<ColumnConfiguration> fields) {
        for (ColumnConfiguration column : fields) {
            if (!message.hasValue(column.getColumnName())) {
                return false;
            }
        }
        return true;
    }

    /*
     * Cases to handle while merging Repeated messages
     * 1. If mergedRepeatedMessage has attribute value null and messageToMerge has not null value, final value will be messageToMerge
     * 2. If mergedRepeatedMessage and messageToMerge has not null and unequal value, final value will be messageToMerge
     *
     * Cases to Ignore
     * 1. If mergedRepeatedMessage has attribute value not null and messageToMerge has null value, final value will be mergedRepeatedMessage
     * 2. If mergedRepeatedMessage and messageToMerge has null value, final value will be mergedRepeatedMessage
     * 3. If mergedRepeatedMessage and messageToMerge has not null and equal value, final value will be mergedRepeatedMessage
     */
    public static RepeatedMessage mergeRepeatedMessages(RepeatedMessage mergedRepeatedMessage, RepeatedMessage messageToMerge) {
        //Ignoring to set system field values, as they are already part of message
        RepeatedMessage resultantMessage = new RepeatedMessage();
        Set<String> allAttributes = extractCombinedAttributes(mergedRepeatedMessage, messageToMerge);

        allAttributes.forEach((attribute) -> {
            //1. If messageToMerge has not null value
            if (messageToMerge.hasValue(attribute)) {
                resultantMessage.putValue(attribute, messageToMerge.getValue(attribute));
            }
            //2. If mergedRepeatedMessage and messageToMerge has not null and unequal value, final value will be messageToMerge
            else if (mergedRepeatedMessage.hasValue(attribute)) {
                resultantMessage.putValue(attribute, mergedRepeatedMessage.getValue(attribute));
            }
        });
        return resultantMessage;
    }

    public static RepeatedMessage CompareAndMergeRepeatedMessages(RepeatedMessage mergedRepeatedMessage, RepeatedMessage messageToMerge) {
        //Ignoring to set system field values, as they are already part of message
        RepeatedMessage resultantMessage = new RepeatedMessage();
        Set<String> allAttributes = extractCombinedAttributes(mergedRepeatedMessage, messageToMerge);

        String currentMessageOperation = ((EnumValue) messageToMerge.getValue(OPERATION)).getValueName();
        String existingMessageOperation = ((EnumValue) mergedRepeatedMessage.getValue(OPERATION)).getValueName();

        List<String> currentMessageNullFields = messageToMerge.getValues().entrySet().stream().filter(entry -> entry.getValue() == null).map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> existingMessageNullFields = mergedRepeatedMessage.getValues().entrySet().stream().filter(entry -> entry.getValue() == null).map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> currentMessageNotNullFields = messageToMerge.getValues().entrySet().stream().filter(entry -> entry.getValue() != null).map(Map.Entry::getKey).collect(Collectors.toList());

        boolean toGenerateMessage = false;

        for (String attribute : allAttributes) {
            // If operation differs between old and new message , then also we have to upsert message.
            if (OPERATION.equals(attribute)) {
                toGenerateMessage = checkOperationAndMergeMessage(currentMessageOperation, existingMessageOperation, attribute, mergedRepeatedMessage, messageToMerge, resultantMessage, toGenerateMessage);
            }
            //Message header fields will not be compared with old message values
            else if (TRANSACTION_TIME_FIELD_NAME.equals(attribute) || QUERY_TIME_FIELD_NAME.equals(attribute) || LAST_PROCESSING_TIME.equals(attribute)) {
                resultantMessage.putValue(attribute, messageToMerge.getValue(attribute));
            }
            // If new message has null value , while it was not null previously , then also generate message.
            else if (isCurrentValueNullAndExistingNotNull(currentMessageNullFields, existingMessageNullFields, attribute)) {
                resultantMessage.putValue(attribute, null);
                toGenerateMessage = true;
            } else if (isCurrentValueNotNullAndExistingIsNull(currentMessageNullFields, existingMessageNullFields, attribute)) {
                resultantMessage.putValue(attribute, messageToMerge.getValue(attribute));
                toGenerateMessage = true;
            }
            // If operation value is same for both old and new message
            else {
                if (checkIfValueChanged(currentMessageNotNullFields, messageToMerge, mergedRepeatedMessage, attribute)) {
                    resultantMessage.putValue(attribute, mergedRepeatedMessage.getValue(attribute));
                } else {
                    resultantMessage.putValue(attribute, messageToMerge.getValue(attribute));
                    toGenerateMessage = true;
                }
            }
        }
        if (toGenerateMessage) {
            return resultantMessage;
        } else {
            return null;
        }
    }

    private static boolean checkOperationAndMergeMessage(String currentMessageOperation, String existingMessageOperation, String attribute, RepeatedMessage mergedRepeatedMessage, RepeatedMessage messageToMerge, RepeatedMessage resultantMessage, boolean toGenerateMessage) {
        if ("UPSERT".equals(currentMessageOperation) && "INSERT".equals(existingMessageOperation)) {
            resultantMessage.putValue(attribute, mergedRepeatedMessage.getValue(attribute));
        } else if (!currentMessageOperation.equals(existingMessageOperation)) {
            resultantMessage.putValue(attribute, messageToMerge.getValue(attribute));
            toGenerateMessage = true;
        } else {
            resultantMessage.putValue(attribute, mergedRepeatedMessage.getValue(attribute));
        }
        return toGenerateMessage;
    }

    private static boolean checkIfValueChanged(List<String> currentMessageNotNullFields, RepeatedMessage messageToMerge, RepeatedMessage mergedRepeatedMessage, String attribute) {
        return (!currentMessageNotNullFields.contains(attribute)) || (messageToMerge.getValue(attribute).equals(mergedRepeatedMessage.getValue(attribute)));
    }

    private static boolean isCurrentValueNotNullAndExistingIsNull(List<String> currentMessageNullFields, List<String> existingMessageNullFields, String attribute) {
        return (!currentMessageNullFields.contains(attribute) && existingMessageNullFields.contains(attribute));
    }

    private static boolean isCurrentValueNullAndExistingNotNull(List<String> currentMessageNullFields, List<String> existingMessageNullFields, String attribute) {
        return (currentMessageNullFields.contains(attribute) && !existingMessageNullFields.contains(attribute));
    }

    private static Set<String> extractCombinedAttributes(RepeatedMessage mergedRepeatedMessage, RepeatedMessage messageToMerge) {
        Map<String, Serializable> currentMessageFields = messageToMerge.getValues();
        Map<String, Serializable> existingMessageFields = mergedRepeatedMessage.getValues();
        Set<String> allAttributes = currentMessageFields.keySet();

        Set<String> attributes = new HashSet<>();
        attributes.addAll(allAttributes);

        existingMessageFields.forEach((attribute, value) -> {
            if (!attributes.contains(attribute)) {
                attributes.add(attribute);
            }
        });
        return attributes;
    }

    public static String getIntermidiateTopicFullName(final String namespace, final String topic) {
        return (org.springframework.util.StringUtils.hasText(namespace) ? namespace + '_' + topic : topic) + TRANSFORMER;
    }
}