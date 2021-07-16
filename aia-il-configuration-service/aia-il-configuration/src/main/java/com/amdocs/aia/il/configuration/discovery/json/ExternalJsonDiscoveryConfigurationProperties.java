package com.amdocs.aia.il.configuration.discovery.json;

import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "aia.il.discovery.json")
@EnableConfigurationProperties
public class ExternalJsonDiscoveryConfigurationProperties {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final SwaggerProcessingRules DEFAULT_PROCESSING_RULES = new SwaggerProcessingRules();

    private String defaultDateFormat = DEFAULT_DATE_FORMAT;
    private Map<ExternalSchemaType, SwaggerProcessingRules> swaggerProcessingRules = new LinkedHashMap<>();

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }

    public Map<ExternalSchemaType, SwaggerProcessingRules> getSwaggerProcessingRules() {
        return swaggerProcessingRules;
    }

    public void setSwaggerProcessingRules(Map<ExternalSchemaType, SwaggerProcessingRules> swaggerProcessingRules) {
        this.swaggerProcessingRules = swaggerProcessingRules;
    }

    public static class SwaggerProcessingRules {
        /**
         * Root entities that are processed in addition to direct API responses
         */
        private List<ProcessedEntityRule> additionalRootEntities = new ArrayList<>();
        private boolean generateEntityForOperationResponse;
        private List<String> forcedOneToOneEntityPathsRegex = new ArrayList<>();
        private List<String> skippedPathsRegex = new ArrayList<>();
        private List<String> jsonInJsonPathsRegex = new ArrayList<>();
        private String sharedEntityKeyPrefix;
        private List<SpecialCharacterReplacement> specialCharacters = new ArrayList<>();


        public String getSharedEntityKeyPrefix() {
            return sharedEntityKeyPrefix;
        }

        public void setSharedEntityKeyPrefix(String sharedEntityKeyPrefix) {
            this.sharedEntityKeyPrefix = sharedEntityKeyPrefix;
        }



        public List<ProcessedEntityRule> getAdditionalRootEntities() {
            return additionalRootEntities;
        }

        public void setAdditionalRootEntities(List<ProcessedEntityRule> additionalRootEntities) {
            this.additionalRootEntities = additionalRootEntities;
        }

        public List<SpecialCharacterReplacement> getSpecialCharacters() {
            return specialCharacters;
        }

        public void setSpecialCharacters(List<SpecialCharacterReplacement> specialCharacters) {
            this.specialCharacters = specialCharacters;
        }

        public boolean isGenerateEntityForOperationResponse() {
            return generateEntityForOperationResponse;
        }

        public void setGenerateEntityForOperationResponse(boolean generateEntityForOperationResponse) {
            this.generateEntityForOperationResponse = generateEntityForOperationResponse;
        }

        public List<String> getForcedOneToOneEntityPathsRegex() {
            return forcedOneToOneEntityPathsRegex;
        }

        public void setForcedOneToOneEntityPathsRegex(List<String> forcedOneToOneEntityPathsRegex) {
            this.forcedOneToOneEntityPathsRegex = forcedOneToOneEntityPathsRegex;
        }

        public List<String> getSkippedPathsRegex() {
            return skippedPathsRegex;
        }

        public void setSkippedPathsRegex(List<String> skippedPathsRegex) {
            this.skippedPathsRegex = skippedPathsRegex;
        }

        public List<String> getJsonInJsonPathsRegex() {
            return jsonInJsonPathsRegex;
        }

        public void setJsonInJsonPathsRegex(List<String> jsonInJsonPathsRegex) {
            this.jsonInJsonPathsRegex = jsonInJsonPathsRegex;
        }
    }

    public static class ProcessedEntityRule {
        private String entityKey;
        private String path;

        public String getEntityKey() {
            return entityKey;
        }

        public void setEntityKey(String entityKey) {
            this.entityKey = entityKey;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class SpecialCharacterReplacement {
        private String specialCharacter;
        private String replaceCharacter;

        public String getSpecialCharacter() {
            return specialCharacter;
        }

        public void setSpecialCharacter(String specialCharacter) {
            this.specialCharacter = specialCharacter;
        }

        public String getReplaceCharacter() {
            return replaceCharacter;
        }

        public void setReplaceCharacter(String replaceCharacter) {
            this.replaceCharacter = replaceCharacter;
        }
    }
}
