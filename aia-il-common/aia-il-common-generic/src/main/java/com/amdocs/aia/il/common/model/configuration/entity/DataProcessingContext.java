package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.properties.RealtimeTransformerConfiguration;
import com.amdocs.aia.il.common.query.IQueryHandler;
import com.amdocs.aia.il.common.query.QueryHandlerFactory;
import com.amdocs.aia.il.common.reference.table.AbstractReferenceTableInfo;
import com.amdocs.aia.il.common.retry.AbstractDataProcessingContextRetryTable;
import com.amdocs.aia.il.common.stores.KeyColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class DataProcessingContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessingContext.class);

    private String name;
    private Map<String, DataProcessingContextRelation> relations;
    private List<DataProcessingContextRelation> rootRelations;
    private Set<String> tables;
    private Map<String, SchemaStore> targetSchemas;

    private final List<PublishingQuery> dataPublishingQueries;
    private final List<PublishingGroovy> dataPublishingGroovies;
    private Map<String, AbstractReferenceTableInfo> referenceTables;

    private AbstractDataProcessingContextRetryTable retryTable;
    private IQueryHandler queryHandler;

    private boolean isDeletedEntitiesQueryDefined;
    private boolean isMandatoryRelationDefined;

    public DataProcessingContext(String name) {
        this.name = name;
        this.relations = new HashMap<>();
        this.rootRelations = new ArrayList<>();
        this.tables = new HashSet<>();
        this.dataPublishingQueries = new ArrayList<>(0);
        this.dataPublishingGroovies = new ArrayList<>(0);
        this.referenceTables = new HashMap<>();
    }

    public void addRelation(DataProcessingContextRelation relation) {
        this.relations.put(relation.getName(), relation);
        if (relation.getType() == RelationType.ROOT) {
            this.rootRelations.add(relation);
        } else {
            this.relations.computeIfAbsent(relation.getParentRelation().getName(), t -> relation.getParentRelation()).registerChildRelation(relation);
            if (relation.isMandatory()) {
                //indicate that there is a mandatory relation in the context
                this.isMandatoryRelationDefined = true;
            }
        }

        if (!this.tables.contains(relation.getTableInfo())) {
            this.tables.add(relation.getTableInfo());
        }
    }

    public void addPublishingQuery(PublishingQuery publishingQuery) {
        LOGGER.debug("Adding data publishing query : {}", publishingQuery);
        this.dataPublishingQueries.add(publishingQuery);
    }

    public void addPublishingGroovy(PublishingGroovy publishingGroovy) {
        LOGGER.debug("Adding data publishing groovy : {}", publishingGroovy);
        this.dataPublishingGroovies.add(publishingGroovy);
    }

    public void addReferenceTableInfo(AbstractReferenceTableInfo referenceTableInfo) {
        LOGGER.debug("Adding data publishing groovy : {}", referenceTableInfo.getName());
        this.referenceTables.put(referenceTableInfo.getName(), referenceTableInfo);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, DataProcessingContextRelation> getRelations() {
        return relations;
    }

    public void setRelations(Map<String, DataProcessingContextRelation> relations) {
        this.relations = relations;
    }

    public List<DataProcessingContextRelation> getRootRelations() {
        return rootRelations;
    }

    public void setTables(Set<String> tables) {
        this.tables = tables;
    }

    public boolean isDeletedEntitiesQueryDefined() {
        return isDeletedEntitiesQueryDefined;
    }

    public void setRootRelations(List<DataProcessingContextRelation> rootRelations) {
        this.rootRelations = rootRelations;
    }

    public Set<String> getTables() {
        return tables;
    }

    public List<PublishingQuery> getDataPublishingQueries() {
        return dataPublishingQueries;
    }

    public List<PublishingGroovy> getDataPublishingGroovies() {
        return dataPublishingGroovies;
    }

    public Map<String, AbstractReferenceTableInfo> getReferenceTables() {
        return referenceTables;
    }

    public boolean isMandatoryRelationDefined() {
        return isMandatoryRelationDefined;
    }

    /**
     * Persist message to retry table
     * Check if exists: if exists calc TTL .. and update, else insert new row
     *
     * @param key     - key of the message to retry
     * @param message - message data to retry
     */
    public void persistRetryMessage(KeyColumn key, RepeatedMessage message) {
        this.retryTable.persist(key.toString(), message);
    }

    /**
     * Refresh a specific reference table only if it belongs to the current Data Processing Context
     */
    public void refreshReferenceTable(AbstractReferenceTableInfo referenceTableInfo, Lock lock) {
        //check if current context contains the input reference table
        if (this.referenceTables.containsKey(referenceTableInfo.getName())) {
            LOGGER.info(LogMsg.getMessage("INFO_LOADING_REFERENCE_TABLE", referenceTableInfo.getName(), this.getName()));
            long numberOfRowsRefreshed = this.queryHandler.refreshReferenceTable(referenceTableInfo, lock);
            LOGGER.info(LogMsg.getMessage("INFO_REFERENCE_TABLE_LOADED_SUCCESSFULLY", referenceTableInfo.getName(), this.getName(), numberOfRowsRefreshed));
        }
    }

    /**
     * Persist new message to retry table. run over the data in case current key exists
     *
     * @param key     - key of the message to retry
     * @param message - message data to retry
     */
    public void persistNewRetryMessage(KeyColumn key, RepeatedMessage message) {
        this.retryTable.persistNew(key.toString(), message);
    }

    public IQueryHandler getQueryHandler() {
        return queryHandler;
    }

    public void initializeTables(QueryHandlerFactory queryHandlerFactory, RealtimeTransformerConfiguration config) {
        if ("Address".equalsIgnoreCase(this.name)) {
            LOGGER.info("Stop");
        }
        // Creating the queryHandler
        this.queryHandler = queryHandlerFactory.getQueryHandler(this.name, config.isLoadReferenceTablesFromDBFile(), config.isBackupDBToFile(), config.getSharedStoragePath(), config.getJobName(), config.isConflictingSubBatches());
        // Creating retry table
        this.retryTable = AbstractDataProcessingContextRetryTable.create(name, config);
        // Creating the tables for relations
        this.tables.forEach(table -> {
            this.relations.values().forEach(relation -> {
                if (relation.getTableInfo().equals(table)) {
                    this.queryHandler.initTable(relation.getConfigurationRow());
                }
            });
        });
        // initialize tables for referenceTables
        this.referenceTables.values().forEach(tableInfo -> {
            this.queryHandler.initReferenceTable(tableInfo);
        });
    }

    public void initializeQueries() {
        // Initializing query and delete statements for delete Publishing query
        this.dataPublishingQueries.forEach(publishingQuery -> {
            //init the statement of the query
            this.queryHandler.initQuery(publishingQuery.getName(), publishingQuery.getQuery());
            //init the deleted entities query
            if (!StringUtils.isEmpty(publishingQuery.getDeletedEntitiesQuery())) {
                //in case a specific query for deleted entities is defined, use it
                this.isDeletedEntitiesQueryDefined = true;
                this.queryHandler.initDeletedEntitiesQuery(publishingQuery.getName(), publishingQuery.getDeletedEntitiesQuery());
            } else {
                //in case no query for deleted entities is defined, use the regular query for the deleted entities
                this.queryHandler.initDeletedEntitiesQuery(publishingQuery.getName(), publishingQuery.getQuery());
            }
        });
    }

    public Map<String, SchemaStore> getTargetSchemas() {
        return targetSchemas;
    }

    public void setTargetSchemas(Map<String, SchemaStore> targetSchemas) {
        this.targetSchemas = targetSchemas;
    }
}