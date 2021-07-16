package com.amdocs.aia.il.deployer;

import com.amdocs.aia.il.deployer.properties.DatabaseProperties;
import com.amdocs.aia.il.deployer.query.ScyllaQueryGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;



@ContextConfiguration(classes = DatabaseProperties.class)
@ExtendWith(MockitoExtension.class)
public class ScyllaQueryGeneratorUnitTest {

    @Mock
    private DatabaseProperties databaseProperties;
    @InjectMocks
    private ScyllaQueryGenerator generator;

    @BeforeEach
    public void setup() {
        when(databaseProperties.getKeyspace()).thenReturn("demo2");
    }


    @Test
    public void createTable() {
        String tableName="bcmapp";
        String declaration = generator.buildCreateStatement(tableName);
        assertEquals("CREATE TABLE IF NOT EXISTS demo2.bcmapp (tablename text,tablepk text,tsversion double,maintabledata text,tablenamecopy text, PRIMARY KEY ((tablename, tablepk),tsversion ) ) WITH CLUSTERING ORDER BY (tsversion DESC);",declaration);
    }

    @Test
    public void createSchemaRelationTable() {
        String tableName="bcmapp_relation";
        String declaration = generator.buildCreateSchemaRelationStatement(tableName);
        assertEquals("CREATE TABLE IF NOT EXISTS demo2.bcmapp_relation (tablename text,tablepk text,contextname text,relationtype text,relationtable text,relationkey text,tsversion double,tablenamecopy text,bulkprocessed boolean, PRIMARY KEY ((tablename, tablepk , contextname), relationtype, relationtable, tsversion,relationkey )) WITH CLUSTERING ORDER BY (relationtype ASC, relationtable ASC, tsversion DESC,relationkey ASC);",declaration);
    }


}