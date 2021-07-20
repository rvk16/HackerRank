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

public class ReplicatorGenCounter implements AuditCounter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicatorGenCounter.class);
    private static final long serialVersionUID = -2723134571887122724L;
    //per correlating entity per entity(contextName) per correlation id
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsLoadedGen;
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsMergedGen;
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsStoredGen;
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsDroppedGen;
    private final Map<String, String> contextLead = new HashMap<>();
    long auditGenerated;

    public ReplicatorGenCounter() {
        initCounters();
    }

    private void initCounters() {
        recordsLoadedGen = new ConcurrentHashMap<>();
        recordsMergedGen = new ConcurrentHashMap<>();
        recordsStoredGen = new ConcurrentHashMap<>();
        recordsDroppedGen = new ConcurrentHashMap<>();
    }

    @Override
    public CounterType getType() {
        return CounterType.REPLICATORGEN;
    }

    @Override
    public void addCounter(Object... args) {
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        String correlationId = (String) args[1]; //correlation id
        Object entity = args[2]; //entity
        //correlating entity
        Object correlatingEntity = args[3];
        Object value = args[4]; //value
        auditGenerated = System.currentTimeMillis();
        if (StringUtils.isNotBlank(correlationId)) {
            switch (counterSubType) {
                case RECORDSLOADEDGEN:
                    updateRecordsLoaded(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                case RECORDSMERGEDGEN:
                    updateRecordsMerged(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                case RECORDSSTOREDGEN:
                    updateRecordsStored(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                case RECORDSDROPPEDGEN:
                    updateRecordsDropped(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateRecordsLoaded(String correlationId, String entity, String correlationEntity, Long value) {
        if (!recordsLoadedGen.containsKey(correlationId)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsLoadedGen.put(correlationId, idValue);
        }
        if (!recordsLoadedGen.get(correlationId).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsLoadedGen.get(correlationId).put(entity, entityValue);
        }
        if (!recordsLoadedGen.get(correlationId).get(entity).containsKey(correlationEntity)) {
            recordsLoadedGen.get(correlationId).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsLoadedGen.get(correlationId).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    private void updateRecordsMerged(String correlationid, String entity, String correlationEntity, Long value) {
        if (!recordsMergedGen.containsKey(correlationid)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsMergedGen.put(correlationid, idValue);
        }
        if (!recordsMergedGen.get(correlationid).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsMergedGen.get(correlationid).put(entity, entityValue);
        }
        if (!recordsMergedGen.get(correlationid).get(entity).containsKey(correlationEntity)) {
            recordsMergedGen.get(correlationid).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsMergedGen.get(correlationid).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    private void updateRecordsStored(String correlationid, String entity, String correlationEntity, Long value) {
        if (!recordsStoredGen.containsKey(correlationid)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsStoredGen.put(correlationid, idValue);
        }
        if (!recordsStoredGen.get(correlationid).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsStoredGen.get(correlationid).put(entity, entityValue);
        }
        if (!recordsStoredGen.get(correlationid).get(entity).containsKey(correlationEntity)) {
            recordsStoredGen.get(correlationid).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsStoredGen.get(correlationid).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    private void updateRecordsDropped(String correlationid, String entity, String correlationEntity, Long value) {
        if (!recordsDroppedGen.containsKey(correlationid)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsDroppedGen.put(correlationid, idValue);
        }
        if (!recordsDroppedGen.get(correlationid).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsDroppedGen.get(correlationid).put(entity, entityValue);
        }
        if (!recordsDroppedGen.get(correlationid).get(entity).containsKey(correlationEntity)) {
            recordsDroppedGen.get(correlationid).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsDroppedGen.get(correlationid).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    @Override
    public List<String> getMessageStructure() {//NOSONAR
        List<String> auditMsgs = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        recordsLoadedGen.forEach((correlationId, contextMap) -> contextMap.forEach((context, entityMap) -> entityMap.forEach((entity, value) -> {

            AuditData auditData = new AuditData();

            auditData.setServiceName("ReplicatorGen");
            auditData.setCorrelationID(correlationId);
            auditData.setEntity(context);
            auditData.setAuditGenerated(auditGenerated);
            auditData.setAuditTime(System.currentTimeMillis());
            if (contextLead.containsKey(context) && contextLead.get(context) != null && contextLead.get(context).equalsIgnoreCase(entity)) {
                auditData.setIsLeadingTable(true);
            }
            auditData.setCorrelatingEntity(entity.toUpperCase());
            auditData.setRecordsLoaded(value);

            if (Boolean.TRUE.equals(checkMergeCounter(correlationId, context, entity))) {
                auditData.setRecordsMerged(recordsMergedGen.get(correlationId).get(context).get(entity));
            }

            if (Boolean.TRUE.equals(checkStoredCounter(correlationId, context, entity))) {
                auditData.setRecordsStored(recordsStoredGen.get(correlationId).get(context).get(entity));
            }

            if (Boolean.TRUE.equals(checkDroppedCounter(correlationId, context, entity))) {
                auditData.setRecordsDropped(recordsDroppedGen.get(correlationId).get(context).get(entity));
            }

            try {
                auditMsgs.add(objectMapper.writeValueAsString(auditData));
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(),e);
            }

        })));

        return auditMsgs;
    }

    public Boolean checkMergeCounter(String correlationId, String context, String entity) {
        return recordsMergedGen.containsKey(correlationId)
                && recordsMergedGen.get(correlationId).containsKey(context)
                && recordsMergedGen.get(correlationId).get(context).containsKey(entity);
    }
    public Boolean checkStoredCounter(String correlationId, String context, String entity) {
        return recordsStoredGen.containsKey(correlationId)
                && recordsStoredGen.get(correlationId).containsKey(context)
                && recordsStoredGen.get(correlationId).get(context).containsKey(entity);
    }
    public Boolean checkDroppedCounter(String correlationId, String context, String entity) {
        return recordsDroppedGen.containsKey(correlationId)
                && recordsDroppedGen.get(correlationId).containsKey(context)
                && recordsDroppedGen.get(correlationId).get(context).containsKey(entity);
    }

    @Override
    public void setContextLead(Map<String, String> contextLead) {
        this.contextLead.putAll(contextLead);
    }

    @Override
    public void clear() {
        initCounters();
    }
}
