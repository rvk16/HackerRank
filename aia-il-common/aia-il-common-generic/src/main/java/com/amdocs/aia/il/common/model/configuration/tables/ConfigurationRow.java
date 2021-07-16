package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.il.common.model.IntegrationLayerAttributeStore;
import com.amdocs.aia.il.common.model.publisher.PublisherEntityStore;
import com.amdocs.aia.il.common.stores.KeyColumnDescriptor;
import com.amdocs.aia.il.common.utils.ConversionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Immutable table configuration row.
 *
 * @author SWARNIMJ
 */
public class ConfigurationRow implements Serializable {
    private static final long serialVersionUID = 2298702954673315146L;

    //focused on data store(hbase)
    private final TableInfoConfiguration tableConfiguration;
    //focused on Datachannel details
    private final PublisherEntityStore entityStore;
    private final List<ColumnConfiguration> pkColumns;
    private final List<ColumnConfiguration> nonPkColumns;
    private final String tableName;
    private final KeyColumnDescriptor idFields;
    private String updateTimeField = null;
    private boolean isRefLeading = false;

    public ConfigurationRow(final PublisherEntityStore entityStore, String typeSystem) {
        this.tableConfiguration = new TableInfoConfiguration();
        this.entityStore = entityStore;
        this.tableName = entityStore.getEntityName();

        List<ColumnConfiguration> nonPkColumnsConfig = new ArrayList<>();
        Map<Integer, ColumnConfiguration> keyPositionToColumn = new TreeMap<>();
        List<ColumnConfiguration> allColumns = new ArrayList<>();
        entityStore.getAttributeStores().forEach(column -> {
            ColumnConfiguration columnConfiguration = new ColumnConfiguration();
            columnConfiguration.setColumnName(column.getAttributeStoreKey());
            columnConfiguration.setLogicalTime(column.isLogicalTime());
            ColumnDatatype columnDatatype = new ColumnDatatype();
            columnDatatype.setSqlType(ConversionUtils.getRawSqlType(column.getType()));
            columnConfiguration.setDatatype(columnDatatype);
            allColumns.add(columnConfiguration);
            if (column.getKeyPosition() != null) {
                keyPositionToColumn.put(column.getKeyPosition(), columnConfiguration);
            } else {
                nonPkColumnsConfig.add(columnConfiguration);
            }
        });

        this.pkColumns = Collections.unmodifiableList(new ArrayList<>(keyPositionToColumn.values()));
        this.tableConfiguration.setColumns(allColumns);
        this.tableConfiguration.setTableName(tableName);
        this.pkColumns.forEach(columnConfiguration -> this.tableConfiguration.getPrimaryKey().getColumnNames().add(columnConfiguration.getColumnName()));
        this.nonPkColumns = Collections.unmodifiableList(nonPkColumnsConfig);
        idFields = new KeyColumnDescriptor(pkColumns);
        this.setUpdateTimeField();
    }

    private void setUpdateTimeField() {
        for (IntegrationLayerAttributeStore column : entityStore.getAttributeStores()) {
            if (column.isUpdateTime()) {
                this.updateTimeField = column.getName();
                break;
            }
        }
    }

    public boolean isRefLeading() {
        return isRefLeading;
    }

    public void setIsRefLeading(boolean refLeading) {
        isRefLeading = refLeading;
    }

    public TableInfoConfiguration getTableConfiguration() {
        return tableConfiguration;
    }

    public PublisherEntityStore getEntityStore() {
        return entityStore;
    }

    public List<ColumnConfiguration> getPkColumns() {
        return pkColumns;
    }

    public List<ColumnConfiguration> getNonPkColumns() {
        return nonPkColumns;
    }

    public String getTableName() {
        return tableName;
    }

    public KeyColumnDescriptor getIdFields() {
        return idFields;
    }

    public String getUpdateTimeField() {
        return updateTimeField;
    }
}