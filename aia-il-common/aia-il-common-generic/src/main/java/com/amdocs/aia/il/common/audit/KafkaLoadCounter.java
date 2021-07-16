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

public class KafkaLoadCounter implements AuditCounter {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaLoadCounter.class);

    //Counters
    private Map<String, AtomicLong> recordsLoaded;
    private long auditGenerated;

    public KafkaLoadCounter() {
        initCounters();
    }

    @Override
    public CounterType getType() {
        return CounterType.KAFKALOAD;
    }

    private void initCounters() {
        recordsLoaded = new ConcurrentHashMap<>();
    }

    public void addCounter(Object... args) {
        //Add audit stats to corresponding counter per entity per correlation id
        String correlationId = (String) args[1]; // correlationId
        Object value = args[2]; // value
        auditGenerated = System.currentTimeMillis();
        if (StringUtils.isNotBlank(correlationId)) {
            incrementRecordsLoaded(correlationId, (Long) value);
        }
    }

    @Override
    public List<String> getMessageStructure() {
        List<String> jsonString = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        recordsLoaded.forEach((correlationID, countValue) -> {
            AuditData auditData = new AuditData();
            auditData.setCorrelationID(correlationID);
            auditData.setEntity("");
            auditData.setRecordsLoaded(countValue);
            auditData.setCorrelatingEntity("");
            auditData.setServiceName("KafkaLoad");
            auditData.setAuditTime(System.currentTimeMillis());
            auditData.setAuditGenerated(auditGenerated);

            try {
                jsonString.add(objectMapper.writeValueAsString(auditData));
            } catch (JsonProcessingException e) {
                LOGGER.error("JSON failure", e);
            }
        });
        return jsonString;
    }

    @Override
    public void setContextLead(Map<String, String> contextLead) {
        //will do later
    }

    public void incrementRecordsLoaded(String correlationId, Long value) {
        if (recordsLoaded.get(correlationId) == null) {
            recordsLoaded.put(correlationId, new AtomicLong());
        }
        if (recordsLoaded.get(correlationId) != null) {
            recordsLoaded.get(correlationId).getAndAccumulate(value, Long::sum);
        }
    }

    @Override
    public void clear() {
        initCounters();
    }
}