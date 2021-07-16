package com.amdocs.aia.il.common.model.configuration.properties;

import java.io.Serializable;

/**
 * Configuration properties.
 *
 * @author SWARNIMJ
 */
@org.springframework.boot.context.properties.ConfigurationProperties(prefix = "aia")
public class ConfigurationProperties implements Serializable {
    private static final long serialVersionUID = 5516049149292872841L;

    private String namespace;
    private String repoElementsLocalPath;
    private boolean debugEnabled;
    private boolean appendMode;
    private boolean unitTestMode=false;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    public String getRepoElementsLocalPath() {
        return repoElementsLocalPath;
    }

    public void setRepoElementsLocalPath(final String repoElementsLocalPath) {
        this.repoElementsLocalPath = repoElementsLocalPath;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(final boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public boolean isAppendMode() {
        return appendMode;
    }

    public void setAppendMode(final boolean appendMode) {
        this.appendMode = appendMode;
    }

    public boolean isUnitTestMode () { return unitTestMode; }

    public void setUnitTestMode (boolean unitTestMode) { this.unitTestMode=unitTestMode; }
}