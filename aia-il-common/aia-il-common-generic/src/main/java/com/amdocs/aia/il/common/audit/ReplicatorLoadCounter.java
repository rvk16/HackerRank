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

public class ReplicatorLoadCounter implements AuditCounter {
    private static final long serialVersionUID = 2152184765060376517L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicatorLoadCounter.class);

    //Counters
    private Map<String, Map<String, AtomicLong>> recordsLoaded;
    private Map<String, Map<String, AtomicLong>> recordsMerged;
    private Map<String, Map<String, AtomicLong>> recordsStored;
    private Map<String, Map<String, AtomicLong>> recordsDeleted;
    private Map<String, Map<String, AtomicLong>> recordsNoChange;
    private Map<String, Map<String, AtomicLong>> recordsError;
    private long auditGenerated;

    public ReplicatorLoadCounter() {
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.REPLICATORLOAD;
    }

    private void initCounters() {
        recordsLoaded = new ConcurrentHashMap<>();
        recordsMerged = new ConcurrentHashMap<>();
        recordsStored = new ConcurrentHashMap<>();
        recordsDeleted = new ConcurrentHashMap<>();
        recordsNoChange = new ConcurrentHashMap<>();
        recordsError = new ConcurrentHashMap<>();
    }

    public void addCounter(Object... args) {
        //Add audit stats to corresponding counter per entity per correlation id
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        String correlationId = (String) args[1]; // correlationId
        Object entity = args[2]; // entity
        Object value = args[3]; // value
        auditGenerated = System.currentTimeMillis();
        if (StringUtils.isNotBlank(correlationId)) {
            switch (counterSubType) {
                case RECORDSLOADED:
                    incrementRecordsLoaded(correlationId, (String) entity, (Long) value);
                    break;
                case RECORDSMERGED:
                    incrementRecordsMerged(correlationId, (String) entity, (Long) value);
                    break;
                case RECORDSSTORED:
                    incrementRecordsStored(correlationId, (String) entity, (Long) value);
                    break;
                case RECORDSDELETED:
                    incrementRecordsDeleted(correlationId, (String) entity, (Long) value);
                    break;
                case RECORDSNOCHANGE:
                    incrementRecordsNoChange(correlationId, (String) entity, (Long) value);
                    break;
                case RECORDSERROR:
                    incrementRecordsError(correlationId, (String) entity, (Long) value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setContextLead(Map<String, String> contextLead) {
        //used in ReplicatorGenCounter
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
        recordsNoChange.get(correlationId).get(entity).getAndAccumulate(value, Long::sum);
    }

    public void incrementRecordsError(String correlationId, String entity, Long value) {
        if (!recordsError.containsKey(correlationId)) {
            Map<String, AtomicLong> entityValue = new ConcurrentHashMap<>();
            recordsError.put(correlationId, entityValue);
        }
        if (!recordsError.get(correlationId).containsKey(entity)) {
            recordsError.get(correlationId).put(entity, new AtomicLong());
        }
        recordsError.get(correlationId).get(entity).getAndAccumulate(value, Long::sum);
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
            auditData.setServiceName("ReplicatorLoad");
            auditData.setAuditTime(System.currentTimeMillis());
            auditData.setAuditGenerated(auditGenerated);
            setMergeDeleteCounter(correlationID, table, auditData);
            setErrorCounter(correlationID, table, auditData);

            try {
                jsonString.add(objectMapper.writeValueAsString(auditData));
            } catch (JsonProcessingException e) {
                LOGGER.error("JSON failure", e);
            }
        }));
        return jsonString;
    }

    public AuditData setMergeDeleteCounter(String correlationID, String table, AuditData auditData) {
        if (recordsMerged.containsKey(correlationID) && recordsMerged.get(correlationID).containsKey(table)) {
            auditData.setRecordsMerged(recordsMerged.get(correlationID).get(table));
        }
        recordLoadDelete(table, correlationID, auditData, recordsStored, recordsDeleted);
        return auditData;
    }

    static void recordLoadDelete(String table, String correlationID, AuditData auditData,
                                 Map<String, Map<String, AtomicLong>> recordsStored,
                                 Map<String, Map<String, AtomicLong>> recordsDeleted) {
        if (recordsStored.containsKey(correlationID) && recordsStored.get(correlationID).containsKey(table)) {
            auditData.setRecordsStored(recordsStored.get(correlationID).get(table));
        }
        if (recordsDeleted.containsKey(correlationID) && recordsDeleted.get(correlationID).containsKey(table)) {
            auditData.setRecordsDeleted(recordsDeleted.get(correlationID).get(table));
        }
    }

    public void setErrorCounter(String correlationID, String table, AuditData auditData) {
        if (recordsError.containsKey(correlationID) && recordsError.get(correlationID).containsKey(table)) {
            auditData.setRecordsError(recordsError.get(correlationID).get(table));
        }
        if (recordsNoChange.containsKey(correlationID) && recordsNoChange.get(correlationID).containsKey(table)) {
            auditData.setRecordsNoChange(recordsNoChange.get(correlationID).get(table));
        }
    }

    @Override
    public void clear() {
        initCounters();
    }
}