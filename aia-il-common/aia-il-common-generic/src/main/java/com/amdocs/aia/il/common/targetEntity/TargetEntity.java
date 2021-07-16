package com.amdocs.aia.il.common.targetEntity;

import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.logical.LogicalEntity;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.stores.KeyColumn;

import java.io.Serializable;
import java.util.*;

public class TargetEntity implements Serializable {

    private static final long serialVersionUID = 4222524826071081420L;
    private final String name;
    private final String namespace;
    private final TargetTable targetTable;
    private ReferentialIntegrityTargetEntityTable riTable;

    public TargetEntity(String name, String namespace, LogicalEntity entity) {
        this.name = name;
        this.namespace = namespace;
        this.targetTable = generateTargetTablesPerTableName(entity);
    }

    private static TargetTable generateTargetTablesPerTableName(LogicalEntity entity) {
        return generateTargetTable(entity);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public TargetTable getTargetTable() {
        return targetTable;
    }

    private static TargetTable generateTargetTable(LogicalEntity entity) {
        Map<String, Datatype> colVsType = new HashMap<>();
        List<String> pk = new ArrayList<>();
        entity.getAttributes().forEach((logicalAttribute -> {
            colVsType.put(logicalAttribute.getAttributeKey(), logicalAttribute.getDatatype());
            if (logicalAttribute.getKeyPosition() != null) {
                pk.add(logicalAttribute.getAttributeKey());
            }
        }));
        return new TargetTable(entity.getEntityKey(), entity.getSchemaKey(), entity.getEntityType(), colVsType, pk);
    }

    public void initReferentialIntegrityTable() {
        this.riTable = new ReferentialIntegrityTargetEntityTable(this);
    }

    /**
     * Persist messages to the referential integrity table
     *
     * @param messages - messages to persist
     */
    public void persistRIMessages(List<RepeatedMessage> messages) {
        this.riTable.persistMessages(messages);
    }

    public static class TargetTable implements Serializable {
        private static final long serialVersionUID = -6280449261267303020L;

        private final String name;
        private final String schemaName;
        private final String entityType;
        private final Map<String, Datatype> columnsVsType;
        private final List<String> primaryKeys;

        public TargetTable(String name, String schemaName, String entityType, Map<String, Datatype> columnsVsType, List<String> primaryKeys) {
            this.name = name;
            this.schemaName = schemaName;
            this.entityType = entityType;
            this.columnsVsType = columnsVsType;
            this.primaryKeys = primaryKeys;
        }

        public String getName() {
            return name;
        }

        public Map<String, Datatype> getColumnsVsType() {
            return columnsVsType;
        }

        public List<String> getPrimaryKeys() {
            return primaryKeys;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public String getEntityType() {
            return this.entityType;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final TargetTable that = (TargetTable) o;
            return Objects.equals(name, that.name) && Objects.equals(schemaName, that.schemaName)
                    && Objects.equals(entityType, that.entityType) && Objects.equals(columnsVsType, that.columnsVsType)
                    && Objects.equals(primaryKeys, that.primaryKeys);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, schemaName, entityType, columnsVsType, primaryKeys);
        }
    }

    @Override
    public String toString() {
        return "TargetEntity{namespace='" + namespace + "', targetTable=" + targetTable + '}';
    }

    /**
     * Check if key is was previously created
     *
     * @param key
     * @return true if key created, else false
     */
    public boolean isKeyPreviouslyCreated(KeyColumn key) {
        return this.riTable.isKeyExists(key);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TargetEntity that = (TargetEntity) o;
        return Objects.equals(name, that.name) && Objects.equals(targetTable, that.targetTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targetTable);
    }
}