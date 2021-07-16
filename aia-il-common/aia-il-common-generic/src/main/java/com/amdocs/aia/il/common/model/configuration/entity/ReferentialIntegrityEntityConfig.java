package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.log.LogMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferentialIntegrityEntityConfig {

    private final static Logger logger = LoggerFactory.getLogger(ReferentialIntegrityEntityConfig.class);

    private final String targetEntityName;
    private Map<String, ForeignKey> foreignKeys;

    public ReferentialIntegrityEntityConfig(String targetEntityName) {
        this.targetEntityName = targetEntityName;
        this.foreignKeys = new HashMap<>();
    }

    public Collection<ForeignKey> getForeignKeys() {
        return foreignKeys.values();
    }

    public String getTargetEntityName() {
        return targetEntityName;
    }

    public void addForeignKey(ForeignKey foreignKey) {
        //validate that FK is not set already by prior groovy file
        validateFK(foreignKey);
        this.foreignKeys.put(foreignKey.getTargetEntityName(), foreignKey);
    }

    /**
     * Validate that FK is not set already by prior groovy file
     * @param foreignKey
     */
    private void validateFK(ForeignKey foreignKey) {
        //validate FK was not defined already
        if(this.foreignKeys.containsKey(foreignKey.getTargetEntityName())) {
            logger.error(LogMsg.getMessage("ERROR_REFERENTIAL_INTEGRITY_FK_ALREADY_DEFINED", foreignKey.getTargetEntityName(), this.getTargetEntityName()));
            throw new RuntimeException(LogMsg.getMessage("ERROR_REFERENTIAL_INTEGRITY_FK_ALREADY_DEFINED", foreignKey.getTargetEntityName(), this.getTargetEntityName()));
        }

        //validate FK is not FK to itself
        if(foreignKey.getTargetEntityName().equals(this.getTargetEntityName())) {
            logger.error(LogMsg.getMessage("ERROR_REFERENTIAL_INTEGRITY_FK_TO_ITSELF", foreignKey.getTargetEntityName()));
            throw new RuntimeException(LogMsg.getMessage("ERROR_REFERENTIAL_INTEGRITY_FK_TO_ITSELF", foreignKey.getTargetEntityName()));
        }
    }

    public void addForeignKey(String targetEntityName, List<String> foreignKeyFieldNames) {
        ForeignKey foreignKey = new ForeignKey(targetEntityName);
        //validate that FK is not set already by prior groovy file
        validateFK(foreignKey);
        foreignKey.setForeignKeyFieldNames(foreignKeyFieldNames);
        this.foreignKeys.put(foreignKey.getTargetEntityName(), foreignKey);
    }



    /**
     * Represents a foreign key from current target entity to other target entity
     */
    public static class ForeignKey {
        private final String targetEntityName;
        private List<String> foreignKeyFieldNames;

        public ForeignKey(String targetEntityName) {
            this.targetEntityName = targetEntityName;
        }

        public String getTargetEntityName() {
            return targetEntityName;
        }

        public List<String> getForeignKeyFieldNames() {
            return foreignKeyFieldNames;
        }

        public void setForeignKeyFieldNames(List<String> foreignKeyFieldNames) {
            this.foreignKeyFieldNames = foreignKeyFieldNames;
        }

        @Override
        public String toString() {
            return "ForeignKey{" +
                    "targetEntityName='" + targetEntityName + '\'' +
                    ", foreignKeyFieldNames=" + foreignKeyFieldNames +
                    '}';
        }
    }
}
