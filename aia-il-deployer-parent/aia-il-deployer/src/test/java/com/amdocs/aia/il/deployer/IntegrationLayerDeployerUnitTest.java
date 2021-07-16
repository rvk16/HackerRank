package com.amdocs.aia.il.deployer;

import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;
import com.amdocs.aia.il.deployer.properties.RuntimeConfiguration;
import com.amdocs.aia.il.deployer.utils.ExecutionReport;
import com.amdocs.aia.repo.client.ElementsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {IntegrationLayerDeployer.class})
class IntegrationLayerDeployerUnitTest {

    @InjectMocks
    private IntegrationLayerDeployer deployer;

    @Mock
    private ElementsProvider elementsProvider;
    @Mock
    private SourceTableDeployerProvider tableDeployerProvider;
    @Mock
    private ExecutionReport executionReport;
    @Mock
    private RuntimeConfiguration runtimeConfiguration;

    @BeforeEach
    public void setup() {
        when(runtimeConfiguration.getRepoElementsLocalPath()).thenReturn("src/test/resources/configurations/add");
    }

   // @Test
    void when_listTables_shouldReturn() {
    IntegrationLayerDeployer spydeployer= Mockito.spy(deployer);
    when(spydeployer.getElementsProvider(anyString())).thenReturn(elementsProvider);
    when(spydeployer.getSchemaStores(elementsProvider)).thenReturn(mockTables());
        when(tableDeployerProvider.getTableDeployer(any())).thenReturn(mock(SourceTableDeployer.class));
        spydeployer.execute();
        verify(executionReport, times(1)).addCreatedTable("table1");
        verify(executionReport, times(1)).addCreatedTable("table2");
        verify(executionReport, times(1)).addCreatedTable("table3");

        verify(executionReport, times(0)).addFailedTable(anyString());
    }

    private static List<PublisherSchemaStore> mockTables() {
        return Arrays.asList(mockTable("table1"), mockTable("table2"), mockTable("table3"));
    }

    private static PublisherSchemaStore mockTable(String entityKey) {
        PublisherSchemaStore table = new PublisherSchemaStore();
        table.setSchemaName(entityKey);
        table.setReference(true);
        return table;
    }
}