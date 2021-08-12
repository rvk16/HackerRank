package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.common.model.extensions.typesystems.SqlTypeSystem;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.configuration.discovery.DiscoveryTestConfiguration;
import com.amdocs.aia.il.configuration.discovery.DiscoveryUtils;
import com.amdocs.aia.il.configuration.discovery.SimpleExternalModelDiscoveryConsumer;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.PROJECT_KEY;
import static com.amdocs.aia.il.configuration.discovery.DiscoveryTestConstants.TEST_USER;
import static com.amdocs.aia.il.configuration.discovery.sql.ExternalSqlDiscoveryParameters.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DiscoveryTestConfiguration.class, ExternalSqlDiscovererTest.class})
public class ExternalSqlDiscovererTest {

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private AiaRepositoryOperations aiaRepositoryOperations;

    @Mock
    private DatabaseIntrospector databaseIntrospector;

    @Autowired
    private ExternalSqlDiscoverer discoverer;

    private SimpleExternalModelDiscoveryConsumer consumer;

    @BeforeEach
    void prepareDiscoverer() {
        consumer = new SimpleExternalModelDiscoveryConsumer();
        discoverer.setConsumer(consumer);
        discoverer.setProjectKey(PROJECT_KEY);
        discoverer.setSchemaType(ExternalSchemaType.ORACLE);
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenReturn(TEST_USER);
    }

    @Test //TODO remove db params
    public void testDiscovery() {
        discoverer.setSchemaType(ExternalSchemaType.ORACLE);
        discoverer.setSchemaName("Charge");

        setDiscoveryParams(ImmutableMap.of(
                CONNECTION_STRING, "jdbc:oracle:thin:@illinbdsi036:1521:BDA4",
                DB_PASSWORD, "ayelet",
                DB_USER, "ayelet",
                DB_TYPE, "ORACLE"));
        discoverer.discover();
        assertEquals(1, consumer.getSchemas().size());
        final ExternalSchema schema = consumer.getSchemas().get(0);
        assertEquals("Charge", schema.getName());
        assertEquals("Charge", schema.getSchemaKey());
        assertEquals(5, consumer.getEntities().size());
        ExternalEntity externalEntityBlBillStmt = consumer.getEntities().get(0);
        assertEquals("BL1_BILL_STATEMENT", externalEntityBlBillStmt.getEntityKey());
        assertEquals(24, externalEntityBlBillStmt.getAttributes().size());
        ExternalEntity externalEntityBlCharge = consumer.getEntities().get(1);
        assertEquals("BL1_CHARGE", externalEntityBlCharge.getEntityKey());
        assertEquals(33, externalEntityBlCharge.getAttributes().size());
        assertEquals("CYCLE_SEQ_RUN", externalEntityBlCharge.getAttributes().get(26).getAttributeKey());
        assertEquals(SqlTypeSystem.INTEGER, externalEntityBlCharge.getAttributes().get(26).getDatatype());
        assertEquals("DYNAMIC_ATTRIBUTE", externalEntityBlCharge.getAttributes().get(25).getAttributeKey());
        assertEquals("VARCHAR(4000)", externalEntityBlCharge.getAttributes().get(25).getDatatype());
        assertEquals("SYS_CREATION_DATE", externalEntityBlCharge.getAttributes().get(4).getAttributeKey());
        assertEquals(SqlTypeSystem.TIMESTAMP, externalEntityBlCharge.getAttributes().get(4).getDatatype());
        assertEquals("AMOUNT", externalEntityBlCharge.getAttributes().get(15).getAttributeKey());
        assertEquals("DOUBLE", externalEntityBlCharge.getAttributes().get(15).getDatatype());
        assertEquals("APPLICATION_ID", externalEntityBlCharge.getAttributes().get(7).getAttributeKey());
        assertEquals("VARCHAR(6)", externalEntityBlCharge.getAttributes().get(7).getDatatype());
        ExternalEntity externalEntityTestData = consumer.getEntities().get(2);
        assertEquals("TEST_DATA", externalEntityTestData.getEntityKey());
        assertEquals(18, externalEntityTestData.getAttributes().size());
        assertEquals("INT_COL", externalEntityTestData.getAttributes().get(0).getAttributeKey());
        assertEquals("BIGINT", externalEntityTestData.getAttributes().get(0).getDatatype());
        assertEquals("VARCHAR_COL", externalEntityTestData.getAttributes().get(1).getAttributeKey());
        assertEquals("VARCHAR(10)", externalEntityTestData.getAttributes().get(1).getDatatype());
        assertEquals("VARCHAR2_COL", externalEntityTestData.getAttributes().get(2).getAttributeKey());
        assertEquals("VARCHAR(10)", externalEntityTestData.getAttributes().get(2).getDatatype());
        assertEquals("NUM_S_P_COL", externalEntityTestData.getAttributes().get(3).getAttributeKey());
        assertEquals("DECIMAL(8,3)", externalEntityTestData.getAttributes().get(3).getDatatype());
        assertEquals("NUM_S_COL", externalEntityTestData.getAttributes().get(4).getAttributeKey());
        assertEquals("BIGINT", externalEntityTestData.getAttributes().get(4).getDatatype());
        assertEquals("NUM_COL", externalEntityTestData.getAttributes().get(5).getAttributeKey());
        assertEquals("DOUBLE", externalEntityTestData.getAttributes().get(5).getDatatype());
        assertEquals("NUMERIC_COL", externalEntityTestData.getAttributes().get(6).getAttributeKey());
        assertEquals("BIGINT", externalEntityTestData.getAttributes().get(6).getDatatype());
        assertEquals("NUMERIC_S_P_COL", externalEntityTestData.getAttributes().get(7).getAttributeKey());
        assertEquals("DECIMAL(5,2)", externalEntityTestData.getAttributes().get(7).getDatatype());
        assertEquals("NUMERIC_S_COL", externalEntityTestData.getAttributes().get(8).getAttributeKey());
        assertEquals("INTEGER", externalEntityTestData.getAttributes().get(8).getDatatype());
        assertEquals("FLOAT_S_COL", externalEntityTestData.getAttributes().get(9).getAttributeKey());
        assertEquals("FLOAT", externalEntityTestData.getAttributes().get(9).getDatatype());
        assertEquals("BIN_FLOAT_COL", externalEntityTestData.getAttributes().get(10).getAttributeKey());
        assertEquals("FLOAT", externalEntityTestData.getAttributes().get(10).getDatatype());
        assertEquals("BFILE_COL", externalEntityTestData.getAttributes().get(11).getAttributeKey());
        assertEquals("VARCHAR(4000)", externalEntityTestData.getAttributes().get(11).getDatatype());
        assertEquals("BIN_DOUBLE_COL", externalEntityTestData.getAttributes().get(12).getAttributeKey());
        assertEquals("DOUBLE", externalEntityTestData.getAttributes().get(12).getDatatype());
        assertEquals("CHAR_COL", externalEntityTestData.getAttributes().get(13).getAttributeKey());
        assertEquals("VARCHAR(1)", externalEntityTestData.getAttributes().get(13).getDatatype());
        assertEquals("CHAR_S_COL", externalEntityTestData.getAttributes().get(14).getAttributeKey());
        assertEquals("VARCHAR(5)", externalEntityTestData.getAttributes().get(14).getDatatype());
        assertEquals("CHARACTER_COL", externalEntityTestData.getAttributes().get(15).getAttributeKey());
        assertEquals("VARCHAR(6)", externalEntityTestData.getAttributes().get(15).getDatatype());
        assertEquals("CHAR_COL2", externalEntityTestData.getAttributes().get(16).getAttributeKey());
        assertEquals("VARCHAR(1)", externalEntityTestData.getAttributes().get(16).getDatatype());


        ExternalEntity externalEntityTestData2 = consumer.getEntities().get(3);
        assertEquals("TEST_DATA2", externalEntityTestData2.getEntityKey());
        assertEquals(11, externalEntityTestData2.getAttributes().size());
        assertEquals("CLOB_COL", externalEntityTestData2.getAttributes().get(0).getAttributeKey());
        assertEquals("VARCHAR(4000)", externalEntityTestData2.getAttributes().get(0).getDatatype());
        assertEquals("DATE_COL", externalEntityTestData2.getAttributes().get(1).getAttributeKey());
        assertEquals("TIMESTAMP", externalEntityTestData2.getAttributes().get(1).getDatatype());
        assertEquals("DEC_S_P_COL", externalEntityTestData2.getAttributes().get(2).getAttributeKey());
        assertEquals("DECIMAL(5,5)", externalEntityTestData2.getAttributes().get(2).getDatatype());
        assertEquals("DEC_S", externalEntityTestData2.getAttributes().get(3).getAttributeKey());
        assertEquals("INTEGER", externalEntityTestData2.getAttributes().get(3).getDatatype());
        assertEquals("DECIMAL_COL", externalEntityTestData2.getAttributes().get(4).getAttributeKey());
        assertEquals("BIGINT", externalEntityTestData2.getAttributes().get(4).getDatatype());
        assertEquals("DECIMAL_S_COL", externalEntityTestData2.getAttributes().get(5).getAttributeKey());
        assertEquals("INTEGER", externalEntityTestData2.getAttributes().get(5).getDatatype());
        assertEquals("DECIMAL_S_P_COL", externalEntityTestData2.getAttributes().get(6).getAttributeKey());
        assertEquals("DECIMAL(9,3)", externalEntityTestData2.getAttributes().get(6).getDatatype());
        assertEquals("DEC_COL", externalEntityTestData2.getAttributes().get(7).getAttributeKey());
        assertEquals("BIGINT", externalEntityTestData2.getAttributes().get(7).getDatatype());
        assertEquals("HTTPURI_COL", externalEntityTestData2.getAttributes().get(8).getAttributeKey());
        assertEquals("VARCHAR(4000)", externalEntityTestData2.getAttributes().get(8).getDatatype());

        ExternalEntity externalEntityTestData3 = consumer.getEntities().get(4);
        assertEquals("TEST_DATA3", externalEntityTestData3.getEntityKey());
        assertEquals(19, externalEntityTestData3.getAttributes().size());

        //verify connection closed
        assertTrue(discoverer.getConnectionManager().isConnectionClosed());
    }

    private void setDiscoveryParams(Map<String, Object> parameters) {
        discoverer.setParameters(DiscoveryUtils.buildTypedDiscoveryParameters(parameters, discoverer.getParametersClass()));
    }
//TODO remove db params
    @Test
    public void testDiscoveryThrowExceptionDBConnectionClosed() {
        when(aiaRepositoryOperations.getUserStatus().getActiveChangeRequest().getCreatedBy()).thenThrow(new RuntimeException());
        discoverer.setSchemaName("Test Sql Schema");
        setDiscoveryParams(ImmutableMap.of(
                CONNECTION_STRING, "jdbc:oracle:thin:@illinbdsi036:1521:BDA4",
                DB_PASSWORD, "ayelet",
                DB_USER, "ayelet",
                DB_TYPE, "ORACLE"));
        assertThrows(RuntimeException.class, ()-> discoverer.discover());
        assertTrue(discoverer.getConnectionManager().isConnectionClosed());
    }

    @Test
    public void testMissingParam() {
        discoverer.setSchemaName("Test Sql Schema");
        assertThrows(ApiException.class, () -> setDiscoveryParams(ImmutableMap.of()));
    }

}
