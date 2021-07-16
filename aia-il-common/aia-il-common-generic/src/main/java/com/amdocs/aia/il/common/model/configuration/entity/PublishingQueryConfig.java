package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.model.configuration.AbstractRTPEntityConfig;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;

public class PublishingQueryConfig extends AbstractRTPEntityConfig {

    private final String dataProcessingContextName;
    private final String query;
    private final String deletedEntitiesQuery;
  //  private final TargetEntity targetEntity;


    public PublishingQueryConfig(String dataProcessingContextName, String query, String deletedEntitiesQuery) {
        super(dataProcessingContextName);
        this.dataProcessingContextName = dataProcessingContextName;
        this.query = query;
        this.deletedEntitiesQuery = deletedEntitiesQuery;
    }

    @Override
    public RTPEntityType getRTPEntityType() {
        return RTPEntityType.CONTEXT_QUERY;
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
}
