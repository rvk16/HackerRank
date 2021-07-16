package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.model.configuration.AbstractRTPEntityConfig;
import com.amdocs.aia.il.common.model.configuration.IRTPRuntimeConfig;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Used for the reference table functionality
 */
public class ReferenceTableConfig extends AbstractRTPEntityConfig implements IRTPRuntimeConfig {

    private List<String> sourceTables;
    private String sql;
    private String sqlFileName;
    private String script;
    private String scriptFileName;
    private boolean published;

    private final RTPEntityType publishingEntityType = RTPEntityType.REFERENCE_TABLE_QUERY;

    public ReferenceTableConfig(String name, List<String> sourceTables, String sql, String script) {
        super(name);
        this.sourceTables = sourceTables;
        this.sql = sql;
        this.script = script;
    }

    public List<String> getSourceTables() {
        return sourceTables;
    }

    public void setSourceTables(List<String> sourceTables) {
        this.sourceTables = sourceTables;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setSqlFileName(String sqlFileName) {
        this.sqlFileName = sqlFileName;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setScriptFileName(String scriptFileName) {
        this.scriptFileName = scriptFileName;
    }

    public String getScript() {
        return script;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public RTPEntityType getRTPEntityType() {
        if (StringUtils.hasText(this.sql) || StringUtils.hasText(this.sqlFileName)) {
            return RTPEntityType.REFERENCE_TABLE_QUERY;
        }
        return RTPEntityType.REFERENCE_TABLE_GROOVY;
    }

    @Override
    public String toString() {
        return "ReferenceTableConfig{" +
                "sourceTables=" + sourceTables +
                ", sql='" + sql + '\'' +
                ", sqlFileName='" + sqlFileName + '\'' +
                ", script='" + script + '\'' +
                ", scriptFileName='" + scriptFileName + '\'' +
                ", published=" + published +
                ", publishingEntityType=" + publishingEntityType +
                '}';
    }
}