package com.amdocs.aia.il.busdeployer;

import com.amdocs.aia.il.busdeployer.logs.Constants;
import com.amdocs.aia.il.busdeployer.logs.LogMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ComponentScan
public class IntegrationLayerBusDeployer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationLayerBusDeployer.class);


    private SchemaEntitiesProcessor schemaEntitiesProcessor;
    private KafkaTopicsCreator kafkaTopicsCreator;

    @Autowired
    public IntegrationLayerBusDeployer(final SchemaEntitiesProcessor schemaEntitiesProcessor,
                                       final KafkaTopicsCreator kafkaTopicsCreator) {
        this.schemaEntitiesProcessor = schemaEntitiesProcessor;
        this.kafkaTopicsCreator = kafkaTopicsCreator;
    }

    public void execute() {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_IL_BUSDEPLOYER_STARTED));
        }
        final List<String> requiredTopics = schemaEntitiesProcessor.getTopicList();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPICS_TO_BE_PROCESSED), String.join(", ", requiredTopics));
        }
        if (!requiredTopics.isEmpty()) {
            kafkaTopicsCreator.createTopics(requiredTopics);
        }

        final List<String> requiredTransformerTopics = schemaEntitiesProcessor.getTransformerTopicList();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPICS_TO_BE_PROCESSED), String.join(", ", requiredTransformerTopics));
        }
        if (!requiredTransformerTopics.isEmpty()) {
            kafkaTopicsCreator.createTransformerTopics(requiredTransformerTopics);
        }

        final List<String> bulkTopics = schemaEntitiesProcessor.getBulkTopicList();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPICS_TO_BE_PROCESSED), String.join(", ", bulkTopics));
        }
        if (!bulkTopics.isEmpty()) {
            kafkaTopicsCreator.createBulkTopics(bulkTopics);
        }

        final List<String> requiredContextTransformerBulkTopics =
                schemaEntitiesProcessor.getContextTransformerBulkTopicList();
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(Constants.MSG_TOPICS_TO_BE_PROCESSED), String.join(", ", requiredContextTransformerBulkTopics));
        }
        if (!requiredContextTransformerBulkTopics.isEmpty()) {
            kafkaTopicsCreator.createContextTransformerBulkTopics(requiredContextTransformerBulkTopics);
        }
    }
}