package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.common.model.repo.ElementVisibility;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;

import java.util.List;

/**
 * Created by SWARNIMJ
 */
public class ReplicaStoreConfiguration extends AbstractPublisherConfigurationModel {
    private static final long serialVersionUID = 4660902043431793074L;

    public static final String ELEMENT_TYPE = getElementTypeFor(ReplicaStoreConfiguration.class);


    @RepoSearchable
    private String tableName;

    private List<ColumnConfiguration> columns;
    private List<TableSourceConfiguration> sources;
    private PrimaryKeyConfiguration primaryKey;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnConfiguration> getColumns() {
        return columns;
    }

    public void setColumns(final List<ColumnConfiguration> columns) {
        this.columns = columns;
    }

    public List<TableSourceConfiguration> getSources() {
        return sources;
    }

    public void setSources(List<TableSourceConfiguration> sources) {
        this.sources = sources;
    }

    public PrimaryKeyConfiguration getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(PrimaryKeyConfiguration primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public ElementVisibility getVisibility() {
        return ElementVisibility.EVERYONE;
    }
}