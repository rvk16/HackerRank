package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.EntityConfigurationUtils;
import com.amdocs.aia.il.common.model.SchemaConfigurationUtils;
import com.amdocs.aia.il.common.model.external.*;
import com.amdocs.aia.il.configuration.service.external.SerializationIDAssigner;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

public abstract class AbstractExternalModelDiscoverer<P extends AbstractExternalModelDiscoveryParameters> implements ExternalModelDiscoverer<P> {
    private String projectKey;
    private ExternalSchemaType schemaType;
    private String schemaName;
    private P parameters;
    private ExternalModelDiscoveryConsumer consumer;

    private final AiaRepositoryOperations repositoryOperations;
    private final SerializationIDAssigner serializationIDAssigner;
    private String defaultSerializationMethod;

    /**
     * The schema that is being created. We keep it as a field (after creation), so that entity discovery
     * will be able to use it (if needed)
     */
    private ExternalSchema schema;

    protected AbstractExternalModelDiscoverer(AiaRepositoryOperations repositoryOperations, SerializationIDAssigner serializationIDAssigner) {
        this.repositoryOperations = repositoryOperations;
        this.serializationIDAssigner = serializationIDAssigner;
    }

    public AiaRepositoryOperations getRepositoryOperations() {
        return repositoryOperations;
    }

    public SerializationIDAssigner getSerializationIDAssigner() {
        return serializationIDAssigner;
    }

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

    public ExternalSchemaType getSchemaType() {
        return schemaType;
    }

    @Override
    public void setSchemaType(ExternalSchemaType schemaType) {
        this.schemaType = schemaType;
    }

    @Override
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public void setParameters(P parameters) {
        this.parameters = parameters;
    }

    public P getParameters() {
        return parameters;
    }

    public ExternalModelDiscoveryConsumer getConsumer() {
        return consumer;
    }

    @Override
    public void setConsumer(ExternalModelDiscoveryConsumer consumer) {
        this.consumer = consumer;
    }

    @Value("${aia.il.default-serialization-method:SharedJson}")
    public void setDefaultSerializationMethod(String defaultSerializationMethod) {
        this.defaultSerializationMethod = defaultSerializationMethod;
    }

    @Override
    public final void discover() {
        try {
            validateInput();
            consumer.discoveryStarted();
            initDiscovery();
            createSchema();
            discoverEntities();
            consumer.discoveryCompleted();
        }catch(RuntimeException e)
        {
            discoveryEnded(false);
            throw e;
        }
        discoveryEnded(true);
    }

    protected void discoveryEnded(boolean isSuccessful) {

    }

    protected void initDiscovery() {
        // can be overridden
    }

    public ExternalSchema getSchema() {
        return schema;
    }

    private void createSchema() {
        schema = new ExternalSchema();
        schema.setProjectKey(projectKey);
        schema.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        schema.setOrigin(ConfigurationConstants.DISCOVERY_ORIGIN);
        schema.setSchemaKey(ModelUtils.toAllowedLocalKey(schemaName));
        schema.setName(schemaName);
        schema.setSchemaType(schemaType);
        schema.setTypeSystem(getTypeSystem());
        schema.setIsReference(getParameters().getReferenceSchema() != null && getParameters().getReferenceSchema());
        schema.setStoreInfo(createSchemaStoreInfo());
        schema.setCollectionRules(createSchemaCollectionRules());
        schema.setDataChannelInfo(createDataChannelInfo());
        schema.setOriginProcess(OriginProcess.IMPLEMENTATION);
        schema.setIsActive(true);
        schema.setCreatedBy(getUserId());
        schema.setCreatedAt(System.currentTimeMillis());
        schema.setId(SchemaConfigurationUtils.getElementId(schema));
        schema.setAvailability((getParameters().getAvailability()) != null ? getParameters().getAvailability():Availability.EXTERNAL);
        schema.setSubjectAreaName((getParameters().getSubjectAreaName()) != null ? getParameters().getSubjectAreaName():"");
        schema.setSubjectAreaKey((getParameters().getSubjectAreaKey()) != null ? getParameters().getSubjectAreaKey():"");
        enrichSchema();
        consumer.acceptSchema(schema);
    }

    protected ExternalEntity createEntity(String entityKey) {
        ExternalEntity entity = new ExternalEntity();
        entity.setProjectKey(getProjectKey());
        entity.setSchemaKey(getSchema().getSchemaKey());
        entity.setSchemaType(getSchema().getSchemaType());
        entity.setEntityKey(entityKey);
        entity.setName(DiscoveryUtils.getNameFromKey(entityKey));
        entity.setReplicationPolicy(ExternalEntityReplicationPolicy.REPLICATE);
        entity.setTypeSystem(getTypeSystem());
        entity.setOriginProcess(getSchema().getOriginProcess());
        entity.setOrigin(getSchema().getOrigin());
        entity.setStoreInfo(createEntityStoreInfo(entityKey));
        entity.setCollectionRules(createEntityCollectionRules(entityKey));
        entity.setIsActive(true);
        entity.setCreatedBy(getUserId());
        entity.setCreatedAt(System.currentTimeMillis());
        entity.setId(EntityConfigurationUtils.getElementId(entity));
        entity.setAvailability(getSchema().getAvailability());
        getSerializationIDAssigner().autoAssignSerializationID(getSchema(), entity);
        entity.setSubjectAreaKey(getSchema().getSubjectAreaKey());
        return entity;
    }

    protected void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalStateException(message);
        }
    }

    protected void requireNonEmpty(String s, String message) {
        if (StringUtils.isEmpty(s)) {
            throw new IllegalStateException(message);
        }
    }

    private void validateInput() {
        requireNonNull(parameters, "Parameters must be provided");
        requireNonNull(schemaType, "Schema type cannot be empty");
        requireNonEmpty(schemaName, "Schema name cannot be empty");
        requireNonEmpty(projectKey, "Project key cannot be empty");
    }

    protected String getUserId() {
        return getRepositoryOperations().getUserStatus().getActiveChangeRequest().getCreatedBy();
    }

    private ExternalSchemaDataChannelInfo createDataChannelInfo() {
        ExternalSchemaDataChannelInfo dataChannelInfo = new ExternalSchemaDataChannelInfo();
        dataChannelInfo.setDataChannelName(schema.getSchemaKey());
        dataChannelInfo.setSerializationMethod(defaultSerializationMethod);
        return dataChannelInfo;
    }

    protected abstract ExternalSchemaStoreInfo createSchemaStoreInfo();

    protected abstract ExternalSchemaCollectionRules createSchemaCollectionRules();

    protected abstract String getTypeSystem();

    protected abstract void discoverEntities();

    protected abstract ExternalEntityCollectionRules createEntityCollectionRules(String entityKey);

    protected abstract ExternalEntityStoreInfo createEntityStoreInfo(String entityKey);
    /**
     * Can be overridden in case the concrete discoverer needs to override or enrich the created schema values
     */
    protected void enrichSchema() {
    }

}
