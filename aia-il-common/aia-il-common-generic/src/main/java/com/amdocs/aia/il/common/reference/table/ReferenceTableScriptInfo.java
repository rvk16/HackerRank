package com.amdocs.aia.il.common.reference.table;

import com.amdocs.aia.il.common.log.LogMsg;
import com.amdocs.aia.il.common.model.configuration.RTPEntityType;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.publisher.AbstractMessageCreator;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.targetEntity.TargetEntity;
import groovy.lang.GroovyClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

/**
 * Holds information about reference table
 * Reference table contains one or more tables, sql to run on this tables
 * Reference table can be published
 * Created by ORENKAF on 7/10/2016.
 */
public class ReferenceTableScriptInfo extends AbstractReferenceTableInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceTableScriptInfo.class);

    public static final String DYNAMIC_CODE_PLACEHOLDER = "DYNAMIC_CODE_PLACEHOLDER";
    private static final long serialVersionUID = -6086805910678684569L;

    private final String script;
    private AbstractMessageCreator groovyEntityMessageCreator;
    private AbstractReferenceTableGroovyScriptWrapper groovyScriptWrapper;

    public ReferenceTableScriptInfo(String name, Map<String, RandomAccessTable> tableAccessInfos, Map<String, ConfigurationRow> tableInfos,
                                    String script, TargetEntity targetEntity, boolean isPublished) {
        super(name, tableAccessInfos, tableInfos, targetEntity, isPublished);
        this.script = script;
        // Create message for Kafka 2 by below
        this.groovyScriptWrapper = initScriptWrapper(this.script);
    }

    public String getScript() {
        return script;
    }

    @Override
    public RTPEntityType getRTPEntityType() {
        return RTPEntityType.REFERENCE_TABLE_GROOVY;
    }

    public AbstractReferenceTableGroovyScriptWrapper getGroovyScriptWrapper() {
        return groovyScriptWrapper;
    }

    /**
     * Initialize the groovy script object with the users groovy script logic from the configuration
     *
     * @param scriptContent - users groovy script logic
     * @return {@link AbstractReferenceTableGroovyScriptWrapper}
     */
    private AbstractReferenceTableGroovyScriptWrapper initScriptWrapper(String scriptContent) {
        try (final GroovyClassLoader gcl = new GroovyClassLoader(this.getClass().getClassLoader());
             final InputStream inputStream = getClass().getResourceAsStream("/ReferenceTableGroovyScriptWrapper.groovy")) {
            final String fileContent;
            try (final Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                fileContent = scanner.useDelimiter("\\A").next();
            }
            //replace the content with the user's script
            String fileContentReplacedWithScript = fileContent.replace(DYNAMIC_CODE_PLACEHOLDER, scriptContent);
            //initialize an instance from the class
            Class cls = gcl.parseClass(fileContentReplacedWithScript);
            AbstractReferenceTableGroovyScriptWrapper scriptWrapper = (AbstractReferenceTableGroovyScriptWrapper) cls.newInstance();
            //set all target entities info since all can be created in the groovy script
            scriptWrapper.setTargetEntity(this.targetEntity);
            scriptWrapper.setName(this.name);
            scriptWrapper.setReferenceTableInfo(this);
            return scriptWrapper;
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(LogMsg.getMessage("ERROR_PARSING_GROOVY_FILE_FOR_REFERENCE_TABLE", scriptContent, name, e.getMessage()), e);
        }
    }

    @Override
    public String toString() {
        return "ReferenceTableScriptInfo{" +
                "script='" + script + '\'' +
                ", groovyEntityMessageCreator=" + groovyEntityMessageCreator +
                "} " + super.toString();
    }
}