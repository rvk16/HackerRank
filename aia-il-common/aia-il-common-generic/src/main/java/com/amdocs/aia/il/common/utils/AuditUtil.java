package com.amdocs.aia.il.common.utils;

public class AuditUtil {
    private boolean isAuditEnable;
    private boolean isAuditLogsEnabled;
    private final String auditEnable;


    public AuditUtil(String auditEnable) {
        this.auditEnable = auditEnable;
    }

    public void setAuditFlag() throws IllegalArgumentException {
        AuditEnableConstant flagValue = AuditEnableConstant.valueOf(auditEnable.toUpperCase());
        switch(flagValue) {
            case NONE:
                this.isAuditEnable = false;
                this.isAuditLogsEnabled = false;
                break;
            case AUDITONLY:
                this.isAuditEnable = true;
                this.isAuditLogsEnabled = false;
                break;
            case AUDITLOGS:
                this.isAuditEnable = true;
                this.isAuditLogsEnabled = true;
                break;
            default:
                throw new IllegalArgumentException("AUDIT_ENABLE_FLAG_IS_EMPTY");
        }
    }

    public boolean isAuditEnabled() {
        return isAuditEnable;
    }

    public void setAuditEnableFlag(boolean isAuditEnable) {
        this.isAuditEnable = isAuditEnable;
    }

    public boolean isAuditLogsEnabled() {
        return isAuditLogsEnabled;
    }

    public void setAuditLogsEnabled(boolean isAuditLogsEnabled) {
        this.isAuditLogsEnabled = isAuditLogsEnabled;
    }

    public enum AuditEnableConstant {
        NONE,
        AUDITONLY,
        AUDITLOGS;
    }
}
