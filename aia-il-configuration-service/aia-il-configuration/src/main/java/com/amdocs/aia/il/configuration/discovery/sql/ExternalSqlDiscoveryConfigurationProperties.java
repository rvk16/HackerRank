package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix = "aia.il.discovery.sql")
@EnableConfigurationProperties
public class ExternalSqlDiscoveryConfigurationProperties {

    private static final String DEFAULT_DATATYPE = LogicalTypeSystem.format(Datatype.forPrimitive(PrimitiveDatatype.STRING));
    public static final int MAX_SCALE = 4;
    public static final int MAX_PRECISION = 15;
    public static final int DEFAULT_VARCHAR_LENGTH = 4000;

    private String defaultDatatype = DEFAULT_DATATYPE;
    private int defaultVarcharLength = DEFAULT_VARCHAR_LENGTH;
    private int maxPrecision = MAX_PRECISION;
    private int maxScale = MAX_SCALE;
    
    public void setDefaultVarcharLength(int defaultVarcharLength) {
        this.defaultVarcharLength = defaultVarcharLength;
    }

    public String getDefaultDatatype() {
        return defaultDatatype;
    }

    public void setDefaultDatatype(String defaultDatatype) {
        this.defaultDatatype = defaultDatatype;
    }

    public int getDefaultVarcharLength() {
        return defaultVarcharLength;
    }

    public int getMaxPrecision() {
        return maxPrecision;
    }

    public void setMaxPrecision(int maxPrecision) {
        this.maxPrecision = maxPrecision;
    }

    public int getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }
}
