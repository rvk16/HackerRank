package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;
import com.amdocs.aia.il.common.model.configuration.properties.RealtimeTransformerConfiguration;
import com.amdocs.aia.il.common.publisher.TrxPublishingInfo;
import com.amdocs.aia.il.common.publisher.messageCreator.QueryEntityMessageCreator;
import com.amdocs.aia.il.common.reference.table.AbstractRTPEntity;
import com.amdocs.aia.il.common.sqlite.queryResult.IQueryResultSet;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;

import java.util.List;
import java.util.Map;

/**
 * Information regarding a publishing query
 * Created by ORENKAF on 11/16/2016.
 */
public class PublishingQuery extends AbstractRTPEntity {

    private final String dataProcessingContextName;
    private final String query;
    private final String deletedEntitiesQuery;
    private final TargetEntity targetEntity;
    private final QueryEntityMessageCreator queryEntityMessageCreator;

    public PublishingQuery(String name, String dataProcessingContextName, String query, String deletedEntitiesQuery,
                           TargetEntity targetEntity) {
        super(name);
        this.dataProcessingContextName = dataProcessingContextName;
        this.query = query;
        this.deletedEntitiesQuery = deletedEntitiesQuery;
        this.targetEntity = targetEntity;
        queryEntityMessageCreator = new QueryEntityMessageCreator(this.name, this.targetEntity);
    }

    /**
     * Convert a {@link IQueryResultSet} to a List of RepeatedMessage
     */
    public List<RepeatedMessage> convertResultSetToMessages(IQueryResultSet resultSet, TrxPublishingInfo trxPublishingInfo,
                                                            boolean isDeletedEntitiesQuery, Map<String, SchemaStore> targetSchemaStoreMap) {
        return this.queryEntityMessageCreator.convertResultSetToMessages (resultSet, trxPublishingInfo, isDeletedEntitiesQuery,targetSchemaStoreMap);
    }

    public TargetEntity getTargetEntity() {
        return targetEntity;
    }

    public String getDataProcessingContextName() {
        return dataProcessingContextName;
    }

    public String getQuery() {
        return query;
    }

    public String getDeletedEntitiesQuery() {
        return deletedEntitiesQuery;
    }

    public void shutdown() {
        // To Be defined while implementation
    }

    public void setRealtimeConfig(RealtimeTransformerConfiguration config) {
        this.queryEntityMessageCreator.setConfig(config);
    }

    @Override
    public RTPEntityType getRTPEntityType() { return RTPEntityType.CONTEXT_QUERY; }

    @Override
    public String toString() {
        return "PublishingQuery{" +
                "query='" + query + '\'' +
                ", deletedEntitiesQuery='" + deletedEntitiesQuery + '\'' +
                ", dataProcessingContextName='" + dataProcessingContextName +
                "} " + super.toString();
    }
}
