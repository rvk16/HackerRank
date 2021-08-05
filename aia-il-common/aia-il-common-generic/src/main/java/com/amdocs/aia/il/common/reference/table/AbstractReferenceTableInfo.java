package com.amdocs.aia.il.common.reference.table;


import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;

import java.util.Map;

/**
 * Base class for reference tables
 * Created by ORENKAF on 5/21/2017.
 */

public abstract class AbstractReferenceTableInfo extends AbstractRTPEntity {

    private static final long serialVersionUID = -6989114073653973205L;
    protected Map<String, RandomAccessTable> tableAccessTableInfos;
    protected final Map<String, ConfigurationRow> tableInfos;
    protected final TargetEntity targetEntity;
    protected final boolean isPublished;

    public AbstractReferenceTableInfo(String name, Map<String, RandomAccessTable> tableAccessTableInfos, Map<String, ConfigurationRow> tableInfos,
                                      TargetEntity targetEntity, boolean isPublished) {
        super(name);
        this.tableAccessTableInfos = tableAccessTableInfos;
        this.tableInfos = tableInfos;
        this.targetEntity = targetEntity;
        this.isPublished = isPublished;
    }

    public boolean isEntityShouldBePublished() {
        return this.isPublished;
    }

    public Map<String, RandomAccessTable> getAccessTableInfos() {
        return tableAccessTableInfos;
    }

    public void setAccessTableInfos(Map<String, RandomAccessTable> tableAccessTableInfos) {
        this.tableAccessTableInfos = tableAccessTableInfos;
    }

    public Map<String, ConfigurationRow> getTableInfos() {
        return tableInfos;
    }

    public TargetEntity getTargetEntity() {
        return targetEntity;
    }

    public void shutdown() {

    }

    @Override
    public String toString() {
        return "AbstractReferenceTableInfo{" +
                "tableInfos=" + tableInfos +
                ", targetEntity=" + targetEntity +
                "} " + super.toString();
    }
}
