package com.amdocs.aia.il.common.audit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AuditLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogger.class);

    public void logAudit(PartitionHandler partitionHandler, boolean auditLogsEnabled) {
        List<JSONObject> auditJsonArray = partitionHandler.getAuditDataList();
        auditJsonArray.forEach(item-> {
            if (item != null && item.length() > 0 && auditLogsEnabled) {
                LOGGER.info("Audit logs for {} ", item);
            }
        });
    }
}