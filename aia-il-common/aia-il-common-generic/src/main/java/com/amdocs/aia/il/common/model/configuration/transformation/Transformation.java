package com.amdocs.aia.il.common.model.configuration.transformation;

import com.amdocs.aia.common.model.DerivedElement;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.transformation.EntityTransformation;
import com.amdocs.aia.common.model.transformation.TransformationContext;
import com.amdocs.aia.common.model.transformation.TransformationContextEntity;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by SWARNIMJ
 */
@JsonIgnoreProperties({"leadKey"}) // to support backward compatibility. The 'leadKey' property was replaced by leadKeys
public class Transformation extends AbstractPublisherConfigurationModel implements DerivedElement<EntityTransformation> {
    private static final long serialVersionUID = -8879624748417208136L;

    public static final String TRANSFORMATION_TYPE = "PublisherTransformation";
    public static final String ELEMENT_TYPE = getElementTypeFor(Transformation.class);

    @RepoSearchable
    private String targetSchemaStoreKey;
    @RepoSearchable
    private String targetEntityStoreKey;
    private TransformationSourceType sourceType = TransformationSourceType.CONTEXT;
    @RepoSearchable
    // relevant in case of sourceType == CONTEXT
    private String contextKey;
    // relevant in case of sourceType == REFERENCE
    private List<TransformationContextEntity> referenceSourceEntities;
    private TransformationImplementationType implementationType;
    private String customScript;
    private String customGroovyScript;
    private String customScriptForDeletionKeys;
    private String customGroovyScriptForDeletionKeys;
    @RepoSearchable
    private String targetSchemaName;
    private boolean isPublished;
    private List<String> referenceAttributes;

    private List<LeadKey> leadKeys;

    public Transformation() {
        super.setElementType(ELEMENT_TYPE);
    }

    public String getTargetSchemaStoreKey() {
        return targetSchemaStoreKey;
    }

    public void setTargetSchemaStoreKey(String targetSchemaStoreKey) {
        this.targetSchemaStoreKey = targetSchemaStoreKey;
    }

    public String getTargetEntityStoreKey() {
        return targetEntityStoreKey;
    }

    public void setTargetEntityStoreKey(String targetEntityStoreKey) {
        this.targetEntityStoreKey = targetEntityStoreKey;
    }
    public List<LeadKey> getLeadKeys() {
        return leadKeys;
    }

    public void setLeadKeys(List<LeadKey> leadKeys) {
        this.leadKeys = leadKeys;
    }
    public String getContextKey() {
        return contextKey;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    public List<TransformationContextEntity> getReferenceSourceEntities() {
        return referenceSourceEntities;
    }

    public void setReferenceSourceEntities(List<TransformationContextEntity> referenceSourceEntities) {
        this.referenceSourceEntities = referenceSourceEntities;
    }

    public TransformationImplementationType getImplementationType() {
        return implementationType;
    }

    public void setImplementationType(TransformationImplementationType implementationType) {
        this.implementationType = implementationType;
    }

    public String getCustomScript() {
        return customScript;
    }

    public void setCustomScript(String customScript) {
        this.customScript = customScript;
    }

    public String getCustomScriptForDeletionKeys() {
        return customScriptForDeletionKeys;
    }

    public void setCustomScriptForDeletionKeys(String customScriptForDeletionKeys) {
        this.customScriptForDeletionKeys = customScriptForDeletionKeys;
    }

    public TransformationSourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(TransformationSourceType sourceType) {
        this.sourceType = sourceType;
    }

    public String getCustomGroovyScript() {
        return customGroovyScript;
    }

    public void setCustomGroovyScript(String customGroovyScript) {
        this.customGroovyScript = customGroovyScript;
    }

    public String getCustomGroovyScriptForDeletionKeys() {
        return customGroovyScriptForDeletionKeys;
    }

    public void setCustomGroovyScriptForDeletionKeys(String customGroovyScriptForDeletionKeys) {
        this.customGroovyScriptForDeletionKeys = customGroovyScriptForDeletionKeys;
    }

    // relevant only in case of sourceType == REFERENCE
    public TransformationContext extractReferenceSharedContext() {
        if (sourceType != TransformationSourceType.REFERENCE) {
            throw new IllegalArgumentException("Cannot extract reference shared context for Publisher Context of source type " + sourceType);
        }
        TransformationContext context = cloneSharedElement(TransformationContext.class);
        context.setContextKey(getSharedContextKey());
        context.setTransformationType(TRANSFORMATION_TYPE);
        context.setContextEntities(this.referenceSourceEntities);
        return context;
    }

    public String getTargetSchemaName() {
        return targetSchemaName;
    }

    public void setTargetSchemaName(String targetSchemaName) {
        this.targetSchemaName = targetSchemaName;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public List<String> getReferenceAttributes() {
        return referenceAttributes;
    }

    public void setReferenceAttributes(List<String> referenceAttributes) {
        this.referenceAttributes = referenceAttributes;
    }


    @JsonIgnore
    public String getSharedContextKey() {
        if (this.sourceType == TransformationSourceType.REFERENCE) {
            return TransformationSourceType.REFERENCE.name();
        } else {
            return this.contextKey;
        }
    }

    @Override
    public EntityTransformation toSharedElement() {
        EntityTransformation transformation = super.cloneSharedElement(EntityTransformation.class);
        transformation.setContextKey(getSharedContextKey());
        transformation.setTransformationType(TRANSFORMATION_TYPE);
        transformation.setTargetSchemaStoreKey(this.targetSchemaStoreKey);
        transformation.setTargetEntityStoreKey(this.targetEntityStoreKey);
        transformation.setSourceElementId(this.getId());
        return transformation;
    }
}