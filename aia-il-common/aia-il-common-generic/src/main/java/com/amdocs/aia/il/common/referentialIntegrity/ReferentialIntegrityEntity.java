package com.amdocs.aia.il.common.referentialIntegrity;

import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ColumnDatatype;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import com.amdocs.aia.il.common.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a referential integrity target entity and contains functionality to create a dummy message
 * for current entity
 */
public class ReferentialIntegrityEntity {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReferentialIntegrityEntity.class);

    private final TargetEntity targetEntity;
    private Map<String, ForeignKey> foreignKeys;

    public ReferentialIntegrityEntity(TargetEntity targetEntity) {
        this.targetEntity = targetEntity;
        this.foreignKeys = new HashMap<>();
    }

    public void addForeignKey(TargetEntity fkTargetEntity, List<String> foreignKeyFieldNames) {
        ForeignKey foreignKey = new ForeignKey(this.targetEntity, fkTargetEntity, foreignKeyFieldNames);
        this.foreignKeys.put(foreignKey.getTargetEntity().getName(), foreignKey);
    }

    public TargetEntity getTargetEntity() {
        return targetEntity;
    }

    public Map<String, ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    /**
     * Represents a foreign key from current target entity to other target entity
     */
    public static class ForeignKey {

        private final TargetEntity parentTargetEntity;
        private final TargetEntity targetEntity;
        private List<ColumnConfiguration> foreignKeyColumns;

        public ForeignKey(TargetEntity parentTargetEntity, TargetEntity targetEntity, List<String> foreignKeyFieldNames) {
            this.parentTargetEntity = parentTargetEntity;
            this.targetEntity = targetEntity;
            this.foreignKeyColumns = new ArrayList<>();
            foreignKeyFieldNames.forEach(this::addForeignKeyColumns);
        }

        public void addForeignKeyColumns(String foreignKeyFieldName) {
            Datatype fkDataType = this.parentTargetEntity.getTargetTable().getColumnsVsType().get(foreignKeyFieldName);
            if (fkDataType == null) {
                LOGGER.error(LogMsg.getMessage("ERROR_REFERENTIAL_INTEGRITY_FK_NOT_EXISTS", foreignKeyFieldName, this.parentTargetEntity.getName()));
                throw new RuntimeException(LogMsg.getMessage("ERROR_REFERENTIAL_INTEGRITY_FK_NOT_EXISTS", foreignKeyFieldName, this.parentTargetEntity.getName()));
            }
            ColumnConfiguration fkColumn = convertToColumnConfiguration(foreignKeyFieldName, fkDataType);
            this.foreignKeyColumns.add(fkColumn);
        }

        private ColumnConfiguration convertToColumnConfiguration(String fkFieldName, Datatype fkDataType) {
            ColumnConfiguration colConfig = new ColumnConfiguration();
            colConfig.setColumnName(fkFieldName);
            ColumnDatatype datatype = new ColumnDatatype();
            datatype.setSqlType(ConversionUtils.getSQLType(fkDataType.getDatatypeKey()));
            colConfig.setDatatype(datatype);
            return colConfig;
        }

        public TargetEntity getParentTargetEntity() {
            return parentTargetEntity;
        }

        public TargetEntity getTargetEntity() {
            return targetEntity;
        }

        public List<ColumnConfiguration> getForeignKeyColumns() {
            return foreignKeyColumns;
        }
    }
}
