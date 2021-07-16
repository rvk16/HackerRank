package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;
import com.amdocs.aia.il.common.model.ConfigurationConstants;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExternalEntity extends AbstractExternalModel {

    public static final String ELEMENT_TYPE = ExternalEntity.class.getSimpleName();
    private static final long serialVersionUID = 5908143132244664470L;

    @RepoSearchable
    private String schemaKey;

    @RepoSearchable
    private String entityKey;

    @RepoSearchable
    private ExternalSchemaType schemaType;

    @RepoSearchable
    private Integer serializationId;

    @RepoSearchable
    private ExternalEntityReplicationPolicy replicationPolicy;

    @RepoSearchable
    private String typeSystem;

    @RepoSearchable
    private boolean isTransaction;

    private List<ExternalAttribute> attributes;

    private ExternalEntityStoreInfo storeInfo;
    private ExternalEntityCollectionRules collectionRules;

    @RepoSearchable
    private Availability availability = Availability.EXTERNAL;

    @RepoSearchable
    private String subjectAreaKey;

    private Map<String, Integer> assignedAttributeSerializationIDs = new LinkedHashMap<>();

    public ExternalEntity() {
        setElementType(ELEMENT_TYPE);
        setProductKey(ConfigurationConstants.PRODUCT_KEY);
    }

    public Map<String, Integer> getAssignedAttributeSerializationIDs() {
        return assignedAttributeSerializationIDs;
    }

    public void setAssignedAttributeSerializationIDs(Map<String, Integer> assignedAttributeSerializationIDs) {
        this.assignedAttributeSerializationIDs = assignedAttributeSerializationIDs;
    }

    public String getSchemaKey() {
        return schemaKey;
    }

    public void setSchemaKey(String schemaKey) {
        this.schemaKey = schemaKey;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public Integer getSerializationId() {
        return serializationId;
    }

    public void setSerializationId(Integer serializationId) {
        this.serializationId = serializationId;
    }

    public ExternalEntityStoreInfo getStoreInfo() {
        return storeInfo;
    }

    public void setStoreInfo(ExternalEntityStoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public List<ExternalAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ExternalAttribute> attributes) {
        this.attributes = attributes;
    }

    public String getTypeSystem() {
        return typeSystem;
    }

    public void setTypeSystem(String typeSystem) {
        this.typeSystem = typeSystem;
    }

    public ExternalEntityCollectionRules getCollectionRules() {
        return collectionRules;
    }

    public void setCollectionRules(ExternalEntityCollectionRules collectionRules) {
        this.collectionRules = collectionRules;
    }

    public ExternalEntityReplicationPolicy getReplicationPolicy() {
        return replicationPolicy;
    }

    public void setReplicationPolicy(ExternalEntityReplicationPolicy replicationPolicy) {
        this.replicationPolicy = replicationPolicy;
    }

    public ExternalSchemaType getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(ExternalSchemaType schemaType) {
        this.schemaType = schemaType;
    }

    public boolean getIsTransaction() {
        return isTransaction;
    }

    public void setIsTransaction(boolean isTransaction) {
        this.isTransaction = isTransaction;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    public String getSubjectAreaKey() {
        return subjectAreaKey;
    }

    public void setSubjectAreaKey(String subjectAreaKey) {
        this.subjectAreaKey = subjectAreaKey;
    }
}
