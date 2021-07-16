package com.amdocs.aia.il.deployer.query;

import com.amdocs.aia.il.deployer.database.ConfigurationConstants;
import com.amdocs.aia.il.deployer.properties.DatabaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScyllaQueryGenerator extends AbstractQueryGenerator {

    private DatabaseProperties databaseProperties;

    @Autowired
    public ScyllaQueryGenerator(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }

    @Override
    public String buildCreateStatement(final String table) {
        return String.format("%s %s.%s %s %s %s",ConfigurationConstants.CREATE_TABLE,databaseProperties.getKeyspace().toLowerCase(),table.toLowerCase(),ConfigurationConstants.QUERY_COLUMN,ConfigurationConstants.PRIMARY_COLUMN,ConfigurationConstants.CLUSTERING_COLUMN);
    }

    @Override
    public String buildAlterStatement(final String table, String tableOption) {
        return ConfigurationConstants.ALTER_TABLE+" "+ databaseProperties.getKeyspace().toLowerCase()+"."+ table.toLowerCase() +" WITH "+tableOption+";";
    }

    @Override
    public String buildCreateSchemaRelationStatement(String table) {
        return ConfigurationConstants.CREATE_TABLE+" "+databaseProperties.getKeyspace()+"."+ table.toLowerCase()+" "+ConfigurationConstants.QUERY_COLUMN_RELATION+ConfigurationConstants.PRIMARY_COLUMN_RELATION+ConfigurationConstants.CLUSTERING_COLUMN_RELATION;
    }
}
