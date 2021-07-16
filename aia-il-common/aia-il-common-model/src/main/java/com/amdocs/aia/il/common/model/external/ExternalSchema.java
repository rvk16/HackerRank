package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;
import com.amdocs.aia.il.common.model.ConfigurationConstants;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExternalSchema extends AbstractExternalModel {

    public static final String ELEMENT_TYPE = ExternalSchema.class.getSimpleName();
    private static final long serialVersionUID = 7800356546782094456L;

    @RepoSearchable
    private String schemaKey;

    @RepoSearchable
    private ExternalSchemaType schemaType;

    @RepoSearchable
    private String typeSystem;

    @RepoSearchable
    private boolean isReference;

    private ExternalSchemaStoreInfo storeInfo;
    private ExternalSchemaCollectionRules collectionRules;
    private ExternalSchemaDataChannelInfo dataChannelInfo;

    @RepoSearchable

    private Availability availability = Availability.EXTERNAL;

    @RepoSearchable
    private String subjectAreaName;

    @RepoSearchable
    private String subjectAreaKey;


    private Map<String, Integer> assignedEntitySerializationIDs = new LinkedHashMap<>();

    public ExternalSchema() {
        setElementType(ELEMENT_TYPE);
        setProductKey(ConfigurationConstants.PRODUCT_KEY);
    }

    public Map<String, Integer> getAssignedEntitySerializationIDs() {
        return assignedEntitySerializationIDs;
    }

    public void setAssignedEntitySerializationIDs(Map<String, Integer> assignedEntitySerializationIDs) {
        this.assignedEntitySerializationIDs = assignedEntitySerializationIDs;
    }

    public void putAssignedEntitySerializationID(String entityKey, int SerializationID) {
        this.assignedEntitySerializationIDs.put(entityKey, SerializationID);
    }

    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getTypeSystem() {
        return typeSystem;
    }

    public void setTypeSystem(String typeSystem) {
        this.typeSystem = typeSystem;
    }

    public boolean getIsReference() {
        return isReference;
    }

    public void setIsReference(boolean isReference) {
        this.isReference = isReference;
    }

    public ExternalSchemaStoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(ExternalSchemaStoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public ExternalSchemaDataChannelInfo getDataChannelInfo() {
        return dataChannelInfo;
    }

    public void setDataChannelInfo(ExternalSchemaDataChannelInfo dataChannelInfo) {
        this.dataChannelInfo = dataChannelInfo;
    }

    public ExternalSchemaType getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(ExternalSchemaType schemaType) {
        this.schemaType = schemaType;
    }

    public ExternalSchemaCollectionRules getCollectionRules() {
        return collectionRules;
    }

    public void setCollectionRules(ExternalSchemaCollectionRules collectionRules) {
        this.collectionRules = collectionRules;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public String getSubjectAreaName() {
        return subjectAreaName;
    }

    public void setSubjectAreaName(String subjectAreaName) {
        this.subjectAreaName = subjectAreaName;
    }

    public String getSubjectAreaKey() {
        return subjectAreaKey;
    }

    public void setSubjectAreaKey(String subjectAreaKey) {
        this.subjectAreaKey = subjectAreaKey;
    }
}
