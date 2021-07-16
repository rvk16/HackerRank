package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.model.configuration.tables.ColumnConfiguration;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.stores.KeyColumnDescriptor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataProcessingContextRelation implements Serializable {

    private String name;
    private String dataProcessingContextName;
    private DataProcessingContextRelation parentRelation;
    private String tableInfo;
    private ConfigurationRow configurationRow;
    private RelationType type;
    private RelationPropagationMode propagationMode;

    //Changed for replicator purpose
    private KeyColumnDescriptor keyColumns;
    private KeyColumnDescriptor parentKeyColumns;

    private List<String> filterKeyColumns;
    private List<String> filterParentKeyColumns;

    private RelationMandatoryMode mandatoryMode;

    private List<DataProcessingContextRelation> childRelations;

    public DataProcessingContextRelation (String name, String dataProcessingContextName, DataProcessingContextRelation parentRelation,
                                          String tableInfo, ConfigurationRow configurationRow, RelationType type, RelationPropagationMode propagationMode,
                                          List<ColumnConfiguration> keyColumns, List<ColumnConfiguration> parentKeyColumns,
                                          List<String> filerKeyColumns, List<String> filterParentKeyColumns,
                                          RelationMandatoryMode mandatoryMode) {
        this.name=name;
        this.dataProcessingContextName=dataProcessingContextName;
        this.parentRelation=parentRelation;
        this.tableInfo=tableInfo;
        this.configurationRow = configurationRow;
        this.type=type;
        this.propagationMode=propagationMode;
        this.keyColumns= keyColumns!=null ? new KeyColumnDescriptor(keyColumns):null;
        this.parentKeyColumns= parentKeyColumns!=null ? new KeyColumnDescriptor(parentKeyColumns):null;
        this.filterKeyColumns=filerKeyColumns;
        this.filterParentKeyColumns=filterParentKeyColumns;
        this.childRelations= new ArrayList<>();
        this.mandatoryMode = mandatoryMode;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name=name;
    }

    public String getDataProcessingContextName () {
        return dataProcessingContextName;
    }

    public void setDataProcessingContextName (String dataProcessingContextName) { this.dataProcessingContextName=dataProcessingContextName; }

    public DataProcessingContextRelation getParentRelation () {
        return parentRelation;
    }

    public void setParentRelation (DataProcessingContextRelation parentRelation) {
        this.parentRelation=parentRelation;
    }

    public String getTableInfo () { return tableInfo; }

    public void setTableInfo (String tableInfo) {
        this.tableInfo=tableInfo;
    }

    public RelationType getType () {
        return type;
    }

    public void setType (RelationType type) {
        this.type=type;
    }

    public RelationPropagationMode getPropagationMode () {
        return propagationMode;
    }

    public void setPropagationMode (RelationPropagationMode propagationMode) {
        this.propagationMode=propagationMode;
    }

    public KeyColumnDescriptor getKeyColumns() {
        return keyColumns;
    }

    public KeyColumnDescriptor getParentKeyColumns() {
        return parentKeyColumns;
    }

    public List<String> getFilterKeyColumns () { return filterKeyColumns; }

    public void setFilterKeyColumns (List<String> filterKeyColumns) {
        this.filterKeyColumns=filterKeyColumns;
    }

    public List<String> getFilterParentKeyColumns () {
        return filterParentKeyColumns;
    }

    public void setFilterParentKeyColumns (List<String> filterParentKeyColumns) { this.filterParentKeyColumns=filterParentKeyColumns; }

    public List<DataProcessingContextRelation> getChildRelations () {
        return childRelations;
    }

    public void setChildRelations (List<DataProcessingContextRelation> childRelations) { this.childRelations=childRelations; }

    public RelationMandatoryMode getMandatoryMode () {
        return mandatoryMode;
    }

    public void setMandatoryMode (RelationMandatoryMode mandatoryMode) {
        this.mandatoryMode=mandatoryMode;
    }

    public ConfigurationRow getConfigurationRow() {
        return configurationRow;
    }

    public boolean isMandatory() {
        return mandatoryMode.equals(RelationMandatoryMode.MANDATORY)
                || mandatoryMode.equals(RelationMandatoryMode.MANDATORY_PUBLISH);
    }

    /**
     * @return true if current relation is mandatory or one of it's childs is mandatory
     */
    public boolean isMandatoryOrIsChildMandatory() {
        boolean isMandatoryOrIsChildMandatory = this.isMandatory();
        for (DataProcessingContextRelation childRelation : this.childRelations) {
            isMandatoryOrIsChildMandatory |= childRelation.isMandatoryOrIsChildMandatory();
        }
        return isMandatoryOrIsChildMandatory;
    }

    public void registerChildRelation(DataProcessingContextRelation relation) {
        this.childRelations.add(relation);
    }

}
