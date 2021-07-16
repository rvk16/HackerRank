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

public class TransientTransformerStoreCounter implements AuditCounter {
    private static final long serialVersionUID = -3276254766691377085L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransientTransformerStoreCounter.class);

    private static final String SERVICE_NAME = "TransientTransformerStore";

    //Counters
    private Map<String, Map<String, AtomicLong>> recordsStored;
    private Map<String, Map<String, AtomicLong>> recordsDeleted;
    private long auditGenerated;

    public TransientTransformerStoreCounter() {
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.TRANSIENTTRANSFORMERSTORE;
    }

    private void initCounters() {
        recordsStored = new ConcurrentHashMap<>();
        recordsDeleted = new ConcurrentHashMap<>();
    }

    public void addCounter(Object... args) {
        //Add audit stats to corresponding counter per entity per correlation id
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        String correlationId = (String) args[1]; // correlationId
        Object context = args[2]; // context
        Object value = args[3]; // value
        auditGenerated = System.currentTimeMillis();
        if (StringUtils.isNotBlank(correlationId)) {
            switch (counterSubType) {
                case RECORDSSTORED:
                    incrementRecordsStored(correlationId, (String) context, (Long) value);
                    break;
                case RECORDSDELETED:
                    incrementRecordsDeleted(correlationId, (String) context, (Long) value);
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
        recordsStored.forEach((correlationID, innerMap) -> innerMap.forEach((context, countValue) -> {
            AuditData auditData = new AuditData();
            auditData.setCorrelationID(correlationID);
            auditData.setEntity(context);
            auditData.setRecordsStored(countValue);
            auditData.setCorrelatingEntity("");
            auditData.setServiceName(SERVICE_NAME);
            auditData.setAuditTime(System.currentTimeMillis());
            auditData.setAuditGenerated(auditGenerated);

            if (recordsDeleted.containsKey(correlationID) && recordsDeleted.get(correlationID).containsKey(context)) {
                auditData.setRecordsDeleted(recordsDeleted.get(correlationID).get(context));
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

    @Override
    public void clear() {
        initCounters();
    }
}