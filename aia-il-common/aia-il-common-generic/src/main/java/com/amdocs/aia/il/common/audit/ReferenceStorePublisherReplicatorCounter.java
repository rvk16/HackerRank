package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.CounterType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ReferenceStorePublisherReplicatorCounter implements AuditCounter {
    private static final long serialVersionUID = -317379243689785025L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceStorePublisherReplicatorCounter.class);

    private static final String SERVICE_NAME = "ReferenceStorePublisherPhase1";

    //Counters
    private Map<String, Map<String, AtomicLong>> recordsLoaded;
    private Map<String, Map<String, AtomicLong>> recordsStored;
    private Map<String, Map<String, AtomicLong>> recordsDeleted;
    private Map<String, Map<String, AtomicLong>> recordsNoChange;
    private Map<String, Map<String, AtomicLong>> recordsMerged;
    private long auditGenerated;

    public ReferenceStorePublisherReplicatorCounter() {
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.REFERENCESTOREPUBLISHERREPLICATOR;
    }

    private void initCounters() {
        recordsLoaded = new ConcurrentHashMap<>();
        recordsMerged = new ConcurrentHashMap<>();
        recordsStored = new ConcurrentHashMap<>();
        recordsDeleted = new ConcurrentHashMap<>();
        recordsNoChange = new ConcurrentHashMap<>();
    }

    public void addCounter(Object... args) {
        //Add audit stats to corresponding counter per entity per correlation id
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        String correlationId = (String)args[1];
        String entity = (String)args[2];
        Long value = (Long)args[3];
        auditGenerated = System.currentTimeMillis();
        if (StringUtils.isNotBlank(correlationId)) {
            switch (counterSubType) {
                case RECORDSLOADED:
                    incrementRecordsLoaded(correlationId, entity, value);
                    break;
                case RECORDSMERGED:
                    incrementRecordsMerged(correlationId, entity, value);
                    break;
                case RECORDSSTORED:
                    incrementRecordsStored(correlationId, entity, value);
                    break;
                case RECORDSDELETED:
                    incrementRecordsDeleted(correlationId, entity, value);
                    break;
                case RECORDSNOCHANGE:
                    incrementRecordsNoChange(correlationId, entity, value);
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
        recordsLoaded.forEach((correlationID, innerMap) -> innerMap.forEach((table, countValue) -> {
            AuditData auditData = new AuditData();
            auditData.setCorrelationID(correlationID);
            auditData.setEntity(table);
            auditData.setRecordsLoaded(countValue);
            auditData.setCorrelatingEntity("");
            auditData.setServiceName(SERVICE_NAME);
            auditData.setAuditTime(System.currentTimeMillis());
            auditData.setAuditGenerated(auditGenerated);

            if (recordsMerged.containsKey(correlationID) && recordsMerged.get(correlationID).containsKey(table)) {
                auditData.setRecordsMerged(recordsMerged.get(correlationID).get(table));
            }
            if (recordsNoChange.containsKey(correlationID) && recordsNoChange.get(correlationID).containsKey(table)) {
                auditData.setRecordsNoChange(recordsNoChange.get(correlationID).get(table));
            }
            ReplicatorLoadCounter.recordStoredDelete(table, correlationID, auditData, recordsStored, recordsDeleted);

            try {
                jsonString.add(objectMapper.writeValueAsString(auditData));
            } catch (JsonProcessingException e) {
                LOGGER.error("JSON failure", e);
            }
        }));
        return jsonString;
    }

    @Override
    public void setContextLead(Map<String, String> contextLead) {
        // empty
    }

    public void incrementRecordsLoaded(String correlationId, String entity, Long value) {
        if (!recordsLoaded.containsKey(correlationId)) {
            Map<String, AtomicLong> entityValue = new ConcurrentHashMap<>();
            recordsLoaded.put(correlationId, entityValue);
        }
        if (!recordsLoaded.get(correlationId).containsKey(entity)) {
            recordsLoaded.get(correlationId).put(entity, new AtomicLong());
        }
        if (recordsLoaded.get(correlationId).get(entity) != null) {
            recordsLoaded.get(correlationId).get(entity).getAndAccumulate(value, Long::sum);
        }
    }

    public void incrementRecordsMerged(String correlationId, String entity, Long value) {
        if (!recordsMerged.containsKey(correlationId)) {
            Map<String, AtomicLong> entityValue = new ConcurrentHashMap<>();
            recordsMerged.put(correlationId, entityValue);
        }
        if (!recordsMerged.get(correlationId).containsKey(entity)) {
            recordsMerged.get(correlationId).put(entity, new AtomicLong());
        }
        if (recordsMerged.get(correlationId).get(entity) != null) {
            recordsMerged.get(correlationId).get(entity).getAndAccumulate(value, Long::sum);
        }
    }

    public void incrementRecordsDeleted(String correlationId, String entity, Long value) {
        if (!recordsDeleted.containsKey(correlationId)) {
            Map<String, AtomicLong> entityValue = new ConcurrentHashMap<>();
            recordsDeleted.put(correlationId, entityValue);
        }
        if (!recordsDeleted.get(correlationId).containsKey(entity)) {
            recordsDeleted.get(correlationId).put(entity, new AtomicLong());
        }
        if (recordsDeleted.get(correlationId).get(entity) != null) {
            recordsDeleted.get(correlationId).get(entity).getAndAccumulate(value, Long::sum);
        }
    }

    public void incrementRecordsStored(String correlationId, String entity, Long value) {
        if (!recordsStored.containsKey(correlationId)) {
            Map<String, AtomicLong> entityValue = new ConcurrentHashMap<>();
            recordsStored.put(correlationId, entityValue);
        }
        if (!recordsStored.get(correlationId).containsKey(entity)) {
            recordsStored.get(correlationId).put(entity, new AtomicLong());
        }
        if (recordsStored.get(correlationId).get(entity) != null) {
            recordsStored.get(correlationId).get(entity).getAndAccumulate(value, Long::sum);
        }
    }

    public void incrementRecordsNoChange(String correlationId, String entity, Long value) {
        if (!recordsNoChange.containsKey(correlationId)) {
            Map<String, AtomicLong> entityValue = new ConcurrentHashMap<>();
            recordsNoChange.put(correlationId, entityValue);
        }
        if (!recordsNoChange.get(correlationId).containsKey(entity)) {
            recordsNoChange.get(correlationId).put(entity, new AtomicLong());
        }
        if (recordsNoChange.get(correlationId).get(entity) != null) {
            recordsNoChange.get(correlationId).get(entity).getAndAccumulate(value, Long::sum);
        }
    }

    @Override
    public void clear() {
        initCounters();
    }
}