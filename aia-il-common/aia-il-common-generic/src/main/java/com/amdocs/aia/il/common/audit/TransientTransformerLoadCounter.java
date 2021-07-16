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

public class TransientTransformerLoadCounter implements AuditCounter {
    private static final long serialVersionUID = -3276254766691377085L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransientTransformerLoadCounter.class);

    private static final String SERVICE_NAME = "TransientTransformerLoad";

    //Counters
    private Map<String, Map<String, AtomicLong>> recordsLoaded;
    private Map<String, Map<String, AtomicLong>> recordsMerged;
    private long auditGenerated;

    public TransientTransformerLoadCounter() {
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.TRANSIENTTRANSFORMERLOAD;
    }

    private void initCounters() {
        recordsLoaded = new ConcurrentHashMap<>();
        recordsMerged = new ConcurrentHashMap<>();
    }

    public void addCounter(Object... args) {
        //Add audit stats to corresponding counter per entity per correlation id
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        String correlationId = (String)args[1]; // correlationId
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

    @Override
    public void clear() {
        initCounters();
    }
}