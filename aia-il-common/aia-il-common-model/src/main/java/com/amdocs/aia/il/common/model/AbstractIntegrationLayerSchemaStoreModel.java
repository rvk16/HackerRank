package com.amdocs.aia.il.common.model;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.common.model.stores.NumericKeyAssignmentPolicy;
import com.amdocs.aia.il.common.model.stores.SchemaStoreCategory;
import com.amdocs.aia.il.common.model.stores.SourceTargetType;
import com.amdocs.aia.il.common.model.stores.StoreTypeCategory;

import java.util.List;
import java.util.Map;

public abstract class AbstractIntegrationLayerSchemaStoreModel extends ProjectElement {
    private static final long serialVersionUID = -4721057941074593343L;

    @RepoSearchable
    private String projectKey;
    @RepoSearchable
    private String schemaName;
    @RepoSearchable
    private String schemaStoreKey;
    @RepoSearchable
    private String logicalSchemaKey;
    @RepoSearchable
    private SourceTargetType sourceTarget;
    @RepoSearchable
    private StoreTypeCategory storeType;
    @RepoSearchable
    private String dataChannel;
    @RepoSearchable
    private String typeSystem;
    @RepoTransient
    private Boolean isReference;
    @RepoSearchable
    private SchemaStoreCategory category;
    @RepoTransient
    private transient List<ElementDependency> dependencies;
    @RepoTransient
    private transient List<ElementPublicFeature> publicFeatures;

    private Availability availability;
    private ExternalSchemaType externalSchemaType;


    private NumericKeyAssignmentPolicy numericKeyAssignmentPolicy;
    private Map<String, Integer> assignedEntityNumericKey;

    protected AbstractIntegrationLayerSchemaStoreModel() {
        super.setElementType(getClass().getSimpleName());
    }

    public static String getElementTypeFor(Class<? extends AbstractIntegrationLayerSchemaStoreModel> modelClass) {
        return modelClass.getSimpleName();
    }

    public String getDataChannelForEntity(final String entityKey) {
        if (availability == Availability.SHARED) {
            return dataChannel;
        }
        return isReference == null || !isReference ? dataChannel + '_' + entityKey.toLowerCase() : dataChannel;
    }

    @Override
    public String getProjectKey() {
        return projectKey;
    }

    @Override
    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaStoreKey() {
        return schemaStoreKey;
    }

    public void setSchemaStoreKey(String schemaStoreKey) {
        this.schemaStoreKey = schemaStoreKey;
    }

    public String getLogicalSchemaKey() {
        return logicalSchemaKey;
    }

    public void setLogicalSchemaKey(String logicalSchemaKey) {
        this.logicalSchemaKey = logicalSchemaKey;
    }

    public SourceTargetType getSourceTarget() {
        return sourceTarget;
    }

    public void setSourceTarget(SourceTargetType sourceTarget) {
        this.sourceTarget = sourceTarget;
    }

    public StoreTypeCategory getStoreType() {
        return storeType;
    }

    public void setStoreType(StoreTypeCategory storeType) {
        this.storeType = storeType;
    }

    public String getDataChannel() {
        return this.dataChannel;
    }

    public void setDataChannel(String dataChannel) {
        this.dataChannel = dataChannel;
    }

    public String getTypeSystem() {
        return typeSystem;
    }

    public void setTypeSystem(String typeSystem) {
        this.typeSystem = typeSystem;
    }

    public Boolean getReference() {
        return isReference;
    }

    public void setReference(Boolean reference) {
        isReference = reference;
    }

    public SchemaStoreCategory getCategory() {
        return category;
    }

    public void setCategory(SchemaStoreCategory category) {
        this.category = category;
    }


    public ExternalSchemaType getExternalSchemaType() {
        return externalSchemaType;
    }

    public void setExternalSchemaType(ExternalSchemaType externalSchemaType) {
        this.externalSchemaType = externalSchemaType;
    }

    public NumericKeyAssignmentPolicy getNumericKeyAssignmentPolicy() {
        return numericKeyAssignmentPolicy;
    }

    public void setNumericKeyAssignmentPolicy(NumericKeyAssignmentPolicy numericKeyAssignmentPolicy) {
        this.numericKeyAssignmentPolicy = numericKeyAssignmentPolicy;
    }

    public Map<String, Integer> getAssignedEntityNumericKey() {
        return assignedEntityNumericKey;
    }

    public void setAssignedEntityNumericKey(Map<String, Integer> assignedEntityNumericKey) {
        this.assignedEntityNumericKey = assignedEntityNumericKey;
    }

    @Override
    public List<ElementDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<ElementDependency> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public List<ElementPublicFeature> getPublicFeatures() {
        return publicFeatures;
    }

    public void setPublicFeatures(List<ElementPublicFeature> publicFeatures) {
        this.publicFeatures = publicFeatures;
    }

    @Override
    public String toString() {
        return "AbstractIntegrationLayerSchemaStoreModel{" +
                "projectKey='" + projectKey + '\'' +
                ", schemaName='" + schemaName + '\'' +
                ", schemaStoreKey='" + schemaStoreKey + '\'' +
                ", logicalSchemaKey='" + logicalSchemaKey + '\'' +
                ", sourceTarget=" + sourceTarget +
                ", storeType=" + storeType +
                ", dataChannel='" + dataChannel + '\'' +
                ", typeSystem='" + typeSystem + '\'' +
                ", isReference=" + isReference +
                ", category=" + category +
                ", dependencies=" + dependencies +
                ", publicFeatures=" + publicFeatures +
                ", numericKeyAssignmentPolicy=" + numericKeyAssignmentPolicy +
                ", assignedEntityNumericKey=" + assignedEntityNumericKey +
                '}';
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }
}