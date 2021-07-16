package com.amdocs.aia.il.common.audit;

import com.amdocs.aia.il.common.publisher.CounterType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class LeadingKeyCounter implements AuditCounter {
    private static final long serialVersionUID = -7453845964787324192L;

    private static final Logger LOGGER = LoggerFactory.getLogger(LeadingKeyCounter.class);

    private static final String SERVICE_NAME = "ComputeLeadingKey";

    //per correlating entity per entity(contextName) per correlation id
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsLoaded;
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsMerged;
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsStored;
    private Map<String, Map<String, Map<String, AtomicLong>>> recordsDropped;
    private final Map<String, String> contextLead = new HashMap<>();
    private long auditGenerated;

    public LeadingKeyCounter() {
        initCounters();
    }

    private void initCounters() {
        recordsLoaded = new ConcurrentHashMap<>();
        recordsMerged = new ConcurrentHashMap<>();
        recordsStored = new ConcurrentHashMap<>();
        recordsDropped = new ConcurrentHashMap<>();
    }

    @Override
    public CounterType getType() {
        return CounterType.COMPUTLEADINGKEY;
    }

    @Override
    public void addCounter(Object... args) {
        CounterType counterSubType = CounterType.valueOf(args[0].toString());
        //correlation id
        String correlationId = (String) args[1];
        //entity
        Object entity = args[2];
        //correlating entity
        Object correlatingEntity = args[3];
        Object value = args[4]; //value
        auditGenerated = System.currentTimeMillis();
        if (StringUtils.isNotBlank(correlationId)) {
            switch (counterSubType) {
                case COMPUTLEADINGKEYLOAD:
                    updateRecordsLoaded(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                case COMPUTLEADINGKEYMERGE:
                    updateRecordsMerged(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                case COMPUTLEADINGKEYSTORE:
                    updateRecordsStored(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                case COMPUTLEADINGKEYDROPPED:
                    updateRecordsDropped(correlationId, (String) entity, (String) correlatingEntity, (Long) value);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateRecordsLoaded(String correlationid, String entity, String correlationEntity, Long value) {
        if (!recordsLoaded.containsKey(correlationid)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsLoaded.put(correlationid, idValue);
        }
        if (!recordsLoaded.get(correlationid).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsLoaded.get(correlationid).put(entity, entityValue);
        }
        if (!recordsLoaded.get(correlationid).get(entity).containsKey(correlationEntity)) {
            recordsLoaded.get(correlationid).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsLoaded.get(correlationid).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    private void updateRecordsMerged(String correlationid, String entity, String correlationEntity, Long value) {
        if (!recordsMerged.containsKey(correlationid)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsMerged.put(correlationid, idValue);
        }
        if (!recordsMerged.get(correlationid).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsMerged.get(correlationid).put(entity, entityValue);
        }
        if (!recordsMerged.get(correlationid).get(entity).containsKey(correlationEntity)) {
            recordsMerged.get(correlationid).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsMerged.get(correlationid).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    private void updateRecordsStored(String correlationid, String entity, String correlationEntity, Long value) {
        if (!recordsStored.containsKey(correlationid)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsStored.put(correlationid, idValue);
        }
        if (!recordsStored.get(correlationid).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsStored.get(correlationid).put(entity, entityValue);
        }
        if (!recordsStored.get(correlationid).get(entity).containsKey(correlationEntity)) {
            recordsStored.get(correlationid).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsStored.get(correlationid).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    private void updateRecordsDropped(String correlationid, String entity, String correlationEntity, Long value) {
        if (!recordsDropped.containsKey(correlationid)) {//NOSONAR
            Map<String, Map<String, AtomicLong>> idValue = new HashMap<>();
            recordsDropped.put(correlationid, idValue);
        }
        if (!recordsDropped.get(correlationid).containsKey(entity)) {
            Map<String, AtomicLong> entityValue = new HashMap<>();
            recordsDropped.get(correlationid).put(entity, entityValue);
        }
        if (!recordsDropped.get(correlationid).get(entity).containsKey(correlationEntity)) {
            recordsDropped.get(correlationid).get(entity).put(correlationEntity, new AtomicLong());
        }
        recordsDropped.get(correlationid).get(entity).get(correlationEntity).getAndAccumulate(value, Long::sum);
    }

    @Override
    public List<String> getMessageStructure() {//NOSONAR
        List<String> auditMsgs = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        recordsLoaded.forEach((correlationId, contextMap) -> contextMap.forEach((context, entityMap) -> entityMap.forEach((entity, value) -> {

            AuditData auditData = new AuditData();

            auditData.setServiceName(SERVICE_NAME);
            auditData.setCorrelationID(correlationId);
            auditData.setEntity(context);
            auditData.setAuditGenerated(auditGenerated);
            auditData.setAuditTime(System.currentTimeMillis());
            if (contextLead.containsKey(context) && contextLead.get(context) != null && contextLead.get(context).equalsIgnoreCase(entity)) {
                auditData.setIsLeadingTable(true);
            }
            auditData.setCorrelatingEntity(entity.toUpperCase());
            auditData.setRecordsLoaded(value);
            if (recordsMerged.containsKey(correlationId)
                    && recordsMerged.get(correlationId).containsKey(context)
                    && recordsMerged.get(correlationId).get(context).containsKey(entity)) {
                auditData.setRecordsMerged(recordsMerged.get(correlationId).get(context).get(entity));
            }

            if (recordsStored.containsKey(correlationId)
                    && recordsStored.get(correlationId).containsKey(context)
                    && recordsStored.get(correlationId).get(context).containsKey(entity)) {
                auditData.setRecordsStored(recordsStored.get(correlationId).get(context).get(entity));
            }

            if (Boolean.TRUE.equals(checkDroppedCounter(correlationId, context, entity))) {
                auditData.setRecordsDropped(recordsDropped.get(correlationId).get(context).get(entity));
            }


            try {
                auditMsgs.add(objectMapper.writeValueAsString(auditData));
            } catch (JsonProcessingException e) {
                LOGGER.error("JSON failure", e);
            }
        })));

        return auditMsgs;
    }

    public Boolean checkDroppedCounter(String correlationId, String context, String entity)
    {
        return recordsDropped.containsKey(correlationId)
                && recordsDropped.get(correlationId).containsKey(context)
                && recordsDropped.get(correlationId).get(context).containsKey(entity);
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