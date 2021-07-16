package com.amdocs.aia.il.collector.deployer.constants;

public final class ConfigurationConstants {
    public static final String APPLICATION_BUNDLE_NAME = "ApplicationResourceBundle";
    public static final String RESOURCE_BUNDLE_PATH = "ResourceBundle";


    public static final String MSG_LIST_OF_CONFIGURED_SCHEMA = "LIST_OF_CONFIGURED_SCHEMA";
    public static final String MSG_NO_TABLES_CONFIGURED = "NO_TABLES_CONFIGURED";
    public static final String MSG_NUMBER_OF_LOADED_SCHEMA_STORE = "NUMBER_OF_LOADED_SCHEMA_STORE";
    public static final String MSG_STARTING_IL_DEPLOYER = "STARTING_IL_DEPLOYER";

    public static final String NAMESPACE = "$NAMESPACE";
    public static final String SERVICE_NAME = "$SERVICE_NAME";
    public static final String IMAGE_REPOSITORY = "$IMAGE_REPOSITORY";
    public static final String DOCKER_IMAGE_NAME = "$DOCKER_IMAGE_NAME";
    public static final String APPLICATION_PROPERTY_FILE = "$APPLICATION_PROPERTY_FILE";
    public static final String INIT_CONTAINER_IMAGE = "$INIT_CONTAINER_IMAGE";
    public static final String KAFKA_SECRET = "$KAFKA_SECRET";
    public static final String SSL = "SSL";


    private ConfigurationConstants() {
        // singleton
    }
}