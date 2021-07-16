package com.amdocs.aia.il.busdeployer.logs;

public final class Constants {

    public static final String PARTITION_COUNT_IS_NOT_PROVIDED = "PartitionCount is not provided for topic %s";
    public static final String REPLICATION_FACTOR_IS_NOT_PROVIDED = "ReplicationFactor is not provided for topic %s";
    public static final String TRANSFORMER = "_transformer";

    public static final String SASL_MECHANISM="sasl.mechanism";
    public static final String SASL="SASL_";
    public static final String SECURITY_PROTOCOL="security.protocol";
    public static final String TLSV="TLSv1.2";
    public static final String JAVA_SECURITY_KRB5_CONF="java.security.krb5.conf";
    public static final String JAVA_SECURITY_AUTH_LOGIN_CONFIG="java.security.auth.login.config";


    public static final String MSG_SETTING_JAVA_SECURITY_AUTH_LOGIN="SETTING_JAVA_SECURITY_AUTH_LOGIN";
    public static final String MSG_SETTING_JAVA_SECURITY_KRB5="SETTING_JAVA_SECURITY_KRB5";
    public static final String MSG_KAFKA_PROPERTY="KAFKA_PROPERTY";
    public static final String MSG_KAFKA_SECURITY_PROTOCOL="KAFKA_SECURITY_PROTOCOL";

    public static final String MSG_IL_BUSDEPLOYER_STARTED="IL_BUSDEPLOYER_STARTED";
    public static final String MSG_TOPICS_TO_BE_PROCESSED="TOPICS_TO_BE_PROCESSED";
    public static final String MSG_EXISTING_TOPICS="EXISTING_TOPICS";
    public static final String MSG_TOPIC_EXIST_SKIPPING="TOPIC_EXIST_SKIPPING";
    public static final String MSG_TOPIC_EXIST_CHECKING_CONFIGURATIONS="TOPIC_EXIST_CHECKING_CONFIGURATIONS";
    public static final String MSG_CREATING_TOPICS_WITH_PARTITION_AND_REPLICATION="CREATING_TOPICS_WITH_PARTITION_AND_REPLICATION";
    public static final String MSG_TOPIC_CREATED_SUCCESSFULLY="TOPIC_CREATED_SUCCESSFULLY";

    public static final String APPLICATION_BUNDLE_NAME = "ApplicationResourceBundle";
    public static final String RESOURCE_BUNDLE_PATH = "ResourceBundle";


    private Constants() {
        // singleton
    }
}