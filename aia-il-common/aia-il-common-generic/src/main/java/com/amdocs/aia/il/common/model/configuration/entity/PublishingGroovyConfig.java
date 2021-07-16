package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.il.common.model.configuration.AbstractRTPEntityConfig;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;

public class PublishingGroovyConfig extends AbstractRTPEntityConfig {

    private final String dataProcessingContextName;
    private final String script;
    private final String deletedEntitiesScript;

    public PublishingGroovyConfig(String dataProcessingContextName, String script, String deletedEntitiesScript) {
        // ToDo need to pass query name from PublisherTransformation Model after it is available.
        super(dataProcessingContextName);
        this.dataProcessingContextName = dataProcessingContextName;
        this.script = script;
        this.deletedEntitiesScript = deletedEntitiesScript;
    }

    @Override
    public RTPEntityType getRTPEntityType() {
        return RTPEntityType.CONTEXT_GROOVY;
    }

    public String getDataProcessingContextName() {
        return dataProcessingContextName;
    }

    public String getScript() {
        return script;
    }

    public String getDeletedEntitiesScript() {
        return deletedEntitiesScript;
    }
}
