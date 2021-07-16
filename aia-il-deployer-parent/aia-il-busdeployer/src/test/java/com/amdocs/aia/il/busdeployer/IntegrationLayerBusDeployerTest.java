package com.amdocs.aia.il.busdeployer;

import com.amdocs.aia.il.busdeployer.exception.ElementsProviderException;
import com.amdocs.aia.il.busdeployer.exception.KafkaTopicsCreatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IntegrationLayerBusDeployer.class, KafkaTopicsCreator.class})
public class IntegrationLayerBusDeployerTest {
    @MockBean
    private SchemaEntitiesProcessor schemaEntitiesProcessor;
    @MockBean
    private KafkaTopicsCreator kafkaTopicsCreator;
    @Autowired
    private IntegrationLayerBusDeployer integrationLayerBusDeployer;

    @Test
    public void shouldReportFailureWhenSchemaProcessorFails() {
        Mockito.when(schemaEntitiesProcessor.getTopicList()).thenThrow(new ElementsProviderException(new RuntimeException("test")));
        assertThrows(ElementsProviderException.class, () -> integrationLayerBusDeployer.execute());
    }

    @Test
    public void shouldCallKafkaCreatorWhenTopicListIsNotEmpty() {
        Mockito.when(schemaEntitiesProcessor.getTopicList()).thenReturn(new ArrayList<>(Arrays.asList("topic1", "topic2")));
        integrationLayerBusDeployer.execute();
        Mockito.verify(kafkaTopicsCreator, Mockito.times(1)).createTopics(ArgumentMatchers.anyList());
    }

    @Test
    public void shouldReportFailureWhenKafkaTopicCreatoeFails() {
        Mockito.when(schemaEntitiesProcessor.getTopicList()).thenReturn(new ArrayList<>(Arrays.asList("topic1", "topic2")));
        Mockito.doThrow(new KafkaTopicsCreatorException(new RuntimeException("test"))).when(kafkaTopicsCreator).createTopics(ArgumentMatchers.anyList());
        assertThrows(KafkaTopicsCreatorException.class, () -> integrationLayerBusDeployer.execute());
    }

    @Test
    public void shouldNotCallKafkaCreatorWhenTopicListIsEmpty() {
        Mockito.when(schemaEntitiesProcessor.getTopicList()).thenReturn(new ArrayList<>());
        integrationLayerBusDeployer.execute();
        Mockito.verify(kafkaTopicsCreator, Mockito.times(0)).createTopics(ArgumentMatchers.anyList());
    }

    @Test
    public void shouldReportFailureWhenOtherErrorsOccur() {
        Mockito.when(schemaEntitiesProcessor.getTopicList()).thenReturn(null);
        assertThrows(NullPointerException.class, () -> integrationLayerBusDeployer.execute());
    }
}