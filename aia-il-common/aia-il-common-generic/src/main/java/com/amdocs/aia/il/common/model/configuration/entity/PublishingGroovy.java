package com.amdocs.aia.il.common.model.configuration.entity;

import com.amdocs.aia.common.serialization.messages.RepeatedMessage;
import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;
import com.amdocs.aia.il.common.publisher.AbstractMessageCreator;
import com.amdocs.aia.il.common.publisher.AbstractPublishingEntity;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import groovy.lang.GroovyClassLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * Information regarding a publishing groovy code
 */
public class PublishingGroovy extends AbstractPublishingEntity {

    private final static Logger LOGGER = LoggerFactory.getLogger(PublishingGroovy.class);

    protected final Map<String, TargetEntity> targetEntities;
    private final String dataProcessingContextName;
    private final String script;
    private final String deletedEntitiesScript;
    private final AbstractDataProcessingGroovyScriptWrapper groovyScriptWrapper;
    private final AbstractDataProcessingGroovyScriptWrapper deletedEntitiesGroovyScriptWrapper;

    public PublishingGroovy(String name, String dataProcessingContextName, String script, String deletedEntitiesScript,
                            Map<String, TargetEntity> targetEntities) {
        super(name);
        this.targetEntities = targetEntities;
        this.dataProcessingContextName = dataProcessingContextName;
        this.script = script;
        this.deletedEntitiesScript = deletedEntitiesScript;
        //initialize the groovy script with the custom code from the configuration
        this.groovyScriptWrapper = initScriptWrapper(this.script);
        if (!StringUtils.isBlank(this.deletedEntitiesScript)) {
            this.deletedEntitiesGroovyScriptWrapper = initScriptWrapper(this.deletedEntitiesScript);
        } else {
            this.deletedEntitiesGroovyScriptWrapper = this.groovyScriptWrapper;
        }
    }


    /**
     * Convert a List of messages values to a List of {@link RepeatedMessage}
     *
     * @return List of {@link RepeatedMessage}
     */
    public List<RepeatedMessage> convertMapToMessages() {
        return null;
    }

    /**
     * Convert a single message values to a {@link RepeatedMessage}
     *
     * @return {@link RepeatedMessage}
     */
    public RepeatedMessage convertMapToMessage() {
        return null;
    }

    /**
     * Initialize the groovy script object with the users groovy script logic from the configuration
     *
     * @param scriptContent - users groovy script logic
     * @return {@link AbstractDataProcessingGroovyScriptWrapper}
     */
    private AbstractDataProcessingGroovyScriptWrapper initScriptWrapper(String scriptContent) {
        try (final GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
             final InputStream inputStream = getClass().getResourceAsStream("/DataProcessingGroovyScriptWrapper.groovy")) {
            final String fileContent;
            try (final Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                fileContent = scanner.useDelimiter("\\A").next();
            }
            //replace the content with the user's script
            String fileContentReplacedWithScript = fileContent.replace("DYNAMIC_CODE_PLACEHOLDER", scriptContent);
            //initialize an instance from the class
            Class cls = gcl.parseClass(fileContentReplacedWithScript);
            AbstractDataProcessingGroovyScriptWrapper scriptWrapper = (AbstractDataProcessingGroovyScriptWrapper) cls.newInstance();
            //set all target entities info since all can be created in the groovy script
            scriptWrapper.setTargetEntities(this.targetEntities);
            scriptWrapper.setName(this.name);
            scriptWrapper.setDataProcessingContextName(this.dataProcessingContextName);
            return scriptWrapper;
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_PARSING_GROOVY_FILE", scriptContent, name, dataProcessingContextName, e.getMessage()), e);
        }
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


    public AbstractDataProcessingGroovyScriptWrapper getGroovyScriptWrapper() {
        return groovyScriptWrapper;
    }

    public AbstractDataProcessingGroovyScriptWrapper getDeletedEntitiesGroovyScriptWrapper() {
        return deletedEntitiesGroovyScriptWrapper;
    }

    @Override
    public RTPEntityType getPublishingEntityType() {
        return RTPEntityType.CONTEXT_GROOVY;
    }

    @Override
    public String toString() {
        return "PublishingGroovy{" +
                "dataProcessingContextName='" + dataProcessingContextName + '\'' +
                ", script='" + script + '\'' +
                ", deletedEntitiesScript='" + deletedEntitiesScript + '\'' +
                "} " + super.toString();
    }
}
