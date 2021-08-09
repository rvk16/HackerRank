package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.CounterType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ReferenceStorePublisherTransformerCounter implements AuditCounter {
    private static final long serialVersionUID = -7513291752079429369L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceStorePublisherTransformerCounter.class);

    private static final String SERVICE_NAME = "ReferenceStorePublisherPhase2";

    //Counters
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsLoaded;
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsStored;
    private final Map<String, String> contextLead = new HashMap<>();
    private long auditGenerated;

    public ReferenceStorePublisherTransformerCounter() {
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.REFERENCESTOREPUBLISHERTRANSFORMER;
    }

    private void initCounters() {
        recordsLoaded = new ConcurrentHashMap<>();
        recordsStored = new ConcurrentHashMap<>();
    }

    public void addCounter(Object... args) {
        //Add audit stats to corresponding counter per entity per correlation id
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        String correlationId = (String) args[1];
        String entity = (String) args[2];
        String correlatingEntity = (String) args[3];
        Long value = (Long) args[4];
        auditGenerated = System.currentTimeMillis();
        if (StringUtils.isNotBlank(correlationId)) {
            switch (counterSubType) {
                case RECORDSLOADED:
                    incrementRecordsLoaded(correlationId, entity, correlatingEntity, value);
                    break;
                case RECORDSSTORED:
                    incrementRecordsStored(correlationId, entity, correlatingEntity, value);
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public List<String> getMessageStructure() {

        List<String> jsonString = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        recordsLoaded.forEach((correlationId, contextMap) -> contextMap.forEach((context, entityMap) -> entityMap.forEach((entity, value) -> {
            AuditData auditData = new AuditData();
            auditData.setCorrelationID(correlationId);
            auditData.setEntity(context);
            auditData.setRecordsLoaded(value);
            auditData.setCorrelatingEntity(entity.toUpperCase(Locale.ROOT));
            auditData.setServiceName(SERVICE_NAME);
            auditData.setAuditTime(System.currentTimeMillis());
            auditData.setAuditGenerated(auditGenerated);
            if (contextLead.containsKey(context) && contextLead.get(context) != null && contextLead.get(context).equalsIgnoreCase(entity)) {
                auditData.setIsLeadingTable(true);
            }
            if (Boolean.TRUE.equals(checkStoredCounter(correlationId, context, entity))) {
                auditData.setRecordsStored(recordsStored.get(correlationId).get(context).get(entity));
            }
            try {
                jsonString.add(objectMapper.writeValueAsString(auditData));
            } catch (JsonProcessingException e) {
                LOGGER.error("JSON failure", e);
            }
        })));
        return jsonString;
    }

    public Boolean checkStoredCounter(String correlationId, String context, String entity)
    {
        return recordsStored.containsKey(correlationId)
                && recordsStored.get(correlationId).containsKey(context)
                && recordsStored.get(correlationId).get(context).containsKey(entity);
    }

    @Override
    public void setContextLead(Map<String, String> contextLead) {
        this.contextLead.putAll(contextLead);
    }

    public void incrementRecordsLoaded(String correlationId, String entity, String correlationEntity, Long value) {
        if (!recordsLoaded.containsKey(correlationId)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsLoaded.put(correlationId, idValue);
        }
        if (!recordsLoaded.get(correlationId).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsLoaded.get(correlationId).put(entity, entityValue);
        }
        if (!recordsLoaded.get(correlationId).get(entity).containsKey(correlationEntity)) {
            recordsLoaded.get(correlationId).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsLoaded.get(correlationId).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    public void incrementRecordsStored(String correlationId, String entity, String correlationEntity, Long value) {
        if (!recordsStored.containsKey(correlationId)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsStored.put(correlationId, idValue);
        }
        if (!recordsStored.get(correlationId).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsStored.get(correlationId).put(entity, entityValue);
        }
        if (!recordsStored.get(correlationId).get(entity).containsKey(correlationEntity)) {
            recordsStored.get(correlationId).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsStored.get(correlationId).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    @Override
    public void clear() {
        initCounters();
    }
}