package com.amdocs.aia.il.configuration.discovery.json;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.external.Availability;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.configuration.discovery.DiscoveryUtils;
import com.amdocs.aia.il.configuration.discovery.json.ExternalJsonDiscoveryConfigurationProperties.SwaggerProcessingRules;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import io.swagger.models.*;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class SwaggerProcessor {
    public static final String PATH_DELIMITER = "/";

    private static final String X_DOX_NOT_IMPLEMENTED = "x-dox-NotImplemented";
    private static final String JSON_IN_JSON_SUFFIX = "(STR)";

    private String filename;
    private String fileContent;

    private final ExternalJsonDiscoveryConfigurationProperties properties;

    private Swagger swagger;
    private Map<String, ProcessedEntityInfo> processedEntities;
    private Map<String, String> specialCharacterToReplaceMap = new HashMap<>();
    private Stack<String> currentFullPath;
    private ExternalSchemaType schemaType;
    private Availability schemaAvailability;
    private SwaggerProcessingRules processingRules;

    SwaggerProcessor(ExternalJsonDiscoveryConfigurationProperties properties) {
        this.properties = properties;
    }

    public void init(ExternalSchemaType schemaType, String filename, String fileContent, Availability schemaAvailability) {
        this.schemaType = schemaType;
        this.filename = filename;
        this.fileContent = fileContent;
        this.schemaAvailability = schemaAvailability;
        this.processingRules = properties.getSwaggerProcessingRules()
                .getOrDefault(schemaType, ExternalJsonDiscoveryConfigurationProperties.DEFAULT_PROCESSING_RULES);
        loadSwaggerFile();
    }

    public String getFilename() {
        return filename;
    }

    public String getFileContent() {
        return fileContent;
    }

    public ExternalJsonDiscoveryConfigurationProperties getProperties() {
        return properties;
    }

    public ExternalSchemaType getSchemaType() {
        return schemaType;
    }

    private void loadSwaggerFile() {
        swagger = new SwaggerParser().parse(fileContent);
        if (swagger == null) {
            throw new ApiException(
                    AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                    AiaApiMessages.GENERAL.DISCOVERY_BAD_SWAGGER_FILE,
                    filename);
        }
    }

    public SwaggerProcessingRules getProcessingRules() {
        return processingRules;
    }

    public String getServiceDescription() {
        final Info info = swagger.getInfo();
        return info != null ? info.getDescription() : "";
    }

    public Map<String, ProcessedEntityInfo> getProcessedEntities() {
        return processedEntities;
    }

    public void process() {
        processedEntities = new HashMap<>();
        specialCharacterToReplaceMap = processingRules.getSpecialCharacters().stream()
                .collect(Collectors.toMap(ExternalJsonDiscoveryConfigurationProperties.SpecialCharacterReplacement::getSpecialCharacter,
                        ExternalJsonDiscoveryConfigurationProperties.SpecialCharacterReplacement::getReplaceCharacter));
        currentFullPath = new Stack<>();
        processFile();
    }

    private void processFile() {
        if (swagger.getPaths() != null) {
            swagger.getPaths().entrySet().stream()
                    .filter(path -> isImplemented(path.getValue().getVendorExtensions()))
                    .flatMap(path -> extractRootOperations(path.getValue()))
                    .forEach(this::processOperation);

            processingRules.getAdditionalRootEntities().forEach(entityRule -> {
                if (getDefinitionByKey(entityRule.getEntityKey()) != null) {
                    processDefinition(entityRule.getEntityKey(), "", entityRule.getPath());
                }
            });
        }
    }

    private void processOperation(Operation operation) {
        final Model model = extractModelFromOperationResponse(operation);
        String entityKey = null;
        if (model instanceof ArrayModel) {
            final Property property = ((ArrayModel) model).getItems();
            if (property instanceof RefProperty) {
                entityKey = ((RefProperty) property).getSimpleRef();
            }
        } else if (model instanceof RefModel) {
            entityKey = ((RefModel) model).getSimpleRef();
        }
        if (entityKey != null) {
            if (processingRules.isGenerateEntityForOperationResponse()) {
                processDefinition(entityKey, "", entityKey);
            } else {
                processChild(entityKey, "","");
            }
        }
    }

    private Model extractModelFromOperationResponse(Operation operation) {
        final Response okResponse = getOkResponse(operation.getResponses());
        if (okResponse != null && okResponse.getResponseSchema() != null) {
            return okResponse.getResponseSchema();
        } else {
            return null;
        }
    }

    private void processDefinition(String entityKey, String parentPath, String childPath) {
        boolean needsProcessing = (!isInSkippedPath()) && addEntityRelativePathIfNeeded(entityKey, parentPath, childPath);

        if (needsProcessing) {
            processChild(entityKey, childPath,parentPath);
        }
    }

    private boolean isInSkippedPath() {
        return isCurrentPathMatches(processingRules.getSkippedPathsRegex());
    }

    private void processChild(String entityKey, String childPath, String parentPath) {
        final Model model = swagger.getDefinitions().get(entityKey);
        getModelProperties(model).forEach((propertyKey, property) -> {
            if (ArrayProperty.isType(property.getType())) {
                // 1:MANY relationship
                final Property itemsProperty = ((ArrayProperty) property).getItems();
                if (RefProperty.isType(itemsProperty.getType(), property.getFormat())) {
                    processOneToManyReference(childPath, propertyKey, (RefProperty) itemsProperty);
                }
            } else if (RefProperty.isType(property.getType(), property.getFormat())) {
                // 1:1 relationship
                processOneToOneReference(entityKey, childPath, propertyKey, (RefProperty) property, new HashSet<>(), propertyKey + "_");
            } else if (!ObjectProperty.isType(property.getType())) {
                 processPrimitive(entityKey, propertyKey, property, null, childPath, parentPath);
            }
        });
    }

    private boolean isCurrentPathMatches(Collection<String> patterns) {
        final String currentPath = getCurrentFormattedPath();
        return patterns.stream().anyMatch(currentPath::matches);
    }

    private String getCurrentFormattedPath() {
        return String.join(PATH_DELIMITER, currentFullPath);
    }

    private void pushToFullPath(String propertyKey) {
        currentFullPath.push(propertyKey);
    }

    private void popFromFullPath(String expectedPropertyKey) {
        final String popped = currentFullPath.pop();
        if (!expectedPropertyKey.equals(popped)) {
            throw new IllegalStateException("Expected " + expectedPropertyKey + " but got " + popped);
        }
    }

    private void processOneToManyReference(String parentPath, String propertyKey, RefProperty property) {
        pushToFullPath(propertyKey);
        final String itemEntityKey = property.getSimpleRef();
        processDefinition(itemEntityKey, parentPath, propertyKey);
        popFromFullPath(propertyKey);
    }

    private void processOneToOneReference(String parentEntityKey, String parentPath, String propertyKey, RefProperty property, Set<String> visited, String attributeKeyPrefix) {
        pushToFullPath(propertyKey);
        if (!isInSkippedPath()) {
            if (shouldForceOnToOneEntityCreation()) {
                processDefinition(property.getSimpleRef(), parentPath, propertyKey);
            } else {
                final String entityKey = property.getSimpleRef();
                if (!visited.contains(entityKey)) {
                    visited.add(entityKey);
                    final String propertyJsonPath = buildEntityJsonPath(parentPath, propertyKey);
                    getOrCreateProcessedEntityInfo(parentEntityKey).addMergedNodeIfNotExists(propertyJsonPath);
                    final Model childModel = getDefinitionByKey(entityKey);
                    getModelProperties(childModel).forEach((childPropertyKey, childProperty) -> {
                        if (ArrayProperty.isType(childProperty.getType())) {
                            final Property itemsProperty = ((ArrayProperty) childProperty).getItems();
                            if (RefProperty.isType(itemsProperty.getType(), childProperty.getFormat())) {
                                processDefinition((((RefProperty) itemsProperty).getSimpleRef()), parentPath, childPropertyKey);
                            }
                        } else if (RefProperty.isType(childProperty.getType(), childProperty.getFormat())) {
                            // currently we drill down only ONE level in 1:1 relation
                            processOneToOneReference(parentEntityKey, propertyKey, childPropertyKey, (RefProperty) childProperty, visited, attributeKeyPrefix + childPropertyKey + "_");
                        } else if (!ObjectProperty.isType(childProperty.getType())) {
                            final String jsonPath = buildAttributeJsonPath(propertyJsonPath, childPropertyKey);
                            final String attributeKey = attributeKeyPrefix + childPropertyKey;
                            processPrimitive(parentEntityKey, attributeKey, childProperty, jsonPath,null,null); // flatten attributes
                        }
                    });
                    visited.remove(entityKey);
                }
            }
        }
        popFromFullPath(propertyKey);
    }

    private boolean shouldForceOnToOneEntityCreation() {
        return isCurrentPathMatches(processingRules.getForcedOneToOneEntityPathsRegex());
    }

    private Map<String, Property> getModelProperties(Model model) {
        Map<String, Property> result;
        if (model == null) {
            result = Collections.emptyMap();
        } else if (model instanceof ComposedModel) {
            result = new HashMap<>();
            ((ComposedModel) model).getAllOf().stream()
                    .map(this::getModelProperties)
                    .forEach(result::putAll);
        } else if (model instanceof RefModel)
            result = getModelProperties(getDefinitionByKey(((RefModel) model).getSimpleRef()));
        else {
            result = model.getProperties();
        }

        if (result != null) {
            result.values().removeIf(p -> !isImplemented(p.getVendorExtensions()));
        } else {
            result = Collections.emptyMap();
        }
        return result;
    }

    private Model getDefinitionByKey(String definitionKey) {
        final Map<String, Model> definitions = swagger.getDefinitions();
        return definitions != null ?
                definitions.get(definitionKey) : null;
    }

    private ProcessedEntityInfo getOrCreateProcessedEntityInfo(String entityKey) {
        return processedEntities.computeIfAbsent(entityKey, this::createProcessedEntityInfo);
    }

    private boolean isProcessedEntityExists(String entityKey) {
        return processedEntities.containsKey(entityKey);
    }


    private ProcessedEntityInfo createProcessedEntityInfo(String entityKey) {
        final ProcessedEntityInfo entityInfo = new ProcessedEntityInfo(entityKey);
        String finalEntityKey = Availability.SHARED.equals(schemaAvailability) && processingRules.getSharedEntityKeyPrefix() != null ?
                processingRules.getSharedEntityKeyPrefix() + entityKey :
                entityKey;
        entityInfo.setFinalEntityKey(finalEntityKey);
        final Model model = getDefinitionByKey(entityKey);
        if (model != null) {
            entityInfo.setDescription(model.getDescription());
            entityInfo.setTitle(model.getTitle());
        }
        return entityInfo;
    }

    private boolean addEntityRelativePathIfNeeded(String entityKey, String parentPath, String childPath) {
        final String propertyJsonPath = buildEntityJsonPath(parentPath, childPath);
        return getOrCreateProcessedEntityInfo(entityKey).addRelativePathIfNotExists(propertyJsonPath);
    }

    private boolean isJsonInJsonPath(String path) {
        return processingRules.getJsonInJsonPathsRegex().stream().anyMatch(path::matches);
    }

    private String combinePaths(String parent, String child) {
        return parent + PATH_DELIMITER + child;
    }

    private String buildEntityJsonPath(String parentPath, String childPath) {
        final String fullPath = getCurrentFormattedPath();
        final String relativePath = combinePaths(parentPath, childPath);
        if (isJsonInJsonPath(fullPath)) {
            return relativePath + JSON_IN_JSON_SUFFIX;
        } else if (currentFullPath.size() >= 2 && currentFullPath.get(currentFullPath.size() - 2).equals(parentPath) && currentFullPath.peek().equals(childPath)) {
            String fullPathToParent = String.join(PATH_DELIMITER, currentFullPath.subList(0, currentFullPath.size() - 1));
            if (isJsonInJsonPath(fullPathToParent)) {
                return combinePaths(parentPath + JSON_IN_JSON_SUFFIX, childPath);
            } else {
                return relativePath;
            }
        } else {
            return relativePath;
        }
    }

    private String buildAttributeJsonPath(String parentPath, String childPath) {
        return combinePaths(parentPath, childPath);
    }

    private void processPrimitive(String entityKey, String attributeKey, Property property, String jsonPath, String childPath,String parentPath ) {
        String originKey = attributeKey;
        if (!specialCharacterToReplaceMap.isEmpty()) {
            attributeKey = replaceSpecialCharacters(attributeKey);
        }
        if (shouldCreateAttribute(entityKey, attributeKey)) {
            final ProcessedAttributeInfo attributeInfo = getOrCreateProcessedEntityInfo(entityKey).getOrCreateProcessedAttribute(attributeKey);
            String datatype = DiscoveryUtils.openApiV2DatatypeToLogical(property.getType(), property.getFormat());
            attributeInfo.setDatatype(datatype);
            attributeInfo.setTitle(property.getTitle());
            attributeInfo.setDescription(property.getDescription());
            if (StringUtils.hasText(jsonPath)) {
                attributeInfo.addJsonPathIfNotExists(jsonPath);
            }else if(!originKey.equals(attributeKey)){
                //if the name of the key was change and we didn't get jsonPath
                attributeInfo.addJsonPathIfNotExists(buildAttributeJsonPath(buildEntityJsonPath(parentPath,childPath),originKey));
            }
        }
    }

    private String replaceSpecialCharacters(String attributeKey) {
        for (Map.Entry<String, String> entry : specialCharacterToReplaceMap.entrySet()) {
            attributeKey = attributeKey.replace(entry.getKey(), entry.getValue());
        }
        return attributeKey;
    }

    private boolean shouldCreateAttribute(String entityKey, String attributeKey) {
        return isProcessedEntityExists(entityKey) && isValidAttributeKey(attributeKey);
    }

    private boolean isValidAttributeKey(String attributeKey) {

        // for now we don't perform manipulations on attribute keys. If the key is invalid - we will not create an attribute for it
        return ModelUtils.toAllowedLocalKey(attributeKey).equals(attributeKey);
    }

    private boolean isImplemented(Map<String, Object> vendorExtensions) {
        return !vendorExtensions.getOrDefault(X_DOX_NOT_IMPLEMENTED, false).equals(true);
    }

    private Stream<Operation> extractRootOperations(Path path) {
        // we process only GET operations
        return Stream.of(path.getGet())
                .filter(Objects::nonNull)
                .filter(operation -> isImplemented(operation.getVendorExtensions()));
    }

    private Response getOkResponse(Map<String, Response> responses) {
        return responses.entrySet().stream()
                .filter(entry -> isOkResponse(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private boolean isOkResponse(String responseKey) {
        switch (responseKey) {
            case "200":
            case "201":
            case "204":
                return true;
            default:
                return false;
        }
    }

    public ProcessedEntityInfo getProcessedEntityInfo(String entityKey) {
        return processedEntities.get(entityKey);
    }

    public ProcessedEntityInfo findProcessedEntityInfoByFinalKey(String finalEntityKey) {
        return processedEntities.values()
                .stream()
                .filter(entityInfo -> entityInfo.getFinalEntityKey().equals(finalEntityKey))
                .findFirst()
                .orElse(null);
    }
}
