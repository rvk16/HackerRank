package com.amdocs.aia.il.common.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassLoaderUtils {

    private ClassLoaderUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Set<String> getScyllaExceptionType() {
        List<String> classes = Arrays.asList("com.datastax.oss.driver.api.core.servererrors.AlreadyExistsException",
                "com.datastax.oss.driver.api.core.servererrors.BootstrappingException",
                "com.datastax.oss.driver.api.core.servererrors.CoordinatorException",
                "com.datastax.oss.driver.api.core.servererrors.FunctionFailureException",
                "com.datastax.oss.driver.api.core.servererrors.InvalidConfigurationInQueryException",
                "com.datastax.oss.driver.api.core.servererrors.InvalidQueryException",
                "com.datastax.oss.driver.api.core.servererrors.OverloadedException",
                "com.datastax.oss.driver.api.core.servererrors.ProtocolError",
                "com.datastax.oss.driver.api.core.servererrors.QueryConsistencyException",
                "com.datastax.oss.driver.api.core.servererrors.QueryExecutionException",
                "com.datastax.oss.driver.api.core.servererrors.QueryValidationException",
                "com.datastax.oss.driver.api.core.servererrors.ReadFailureException",
                "com.datastax.oss.driver.api.core.servererrors.ReadTimeoutException",
                "com.datastax.oss.driver.api.core.servererrors.ServerError",
                "com.datastax.oss.driver.api.core.servererrors.SyntaxError",
                "com.datastax.oss.driver.api.core.servererrors.TruncateException",
                "com.datastax.oss.driver.api.core.servererrors.UnauthorizedException",
                "com.datastax.oss.driver.api.core.servererrors.UnavailableException",
                "com.datastax.oss.driver.api.core.servererrors.WriteFailureException",
                "com.datastax.oss.driver.api.core.servererrors.WriteTimeoutException",
                "com.datastax.oss.driver.api.core.NoNodeAvailableException");

        return new HashSet<>(classes);
    }

    public static Set<String> getKafkaExceptionType() {
        Set<String> allowedExceptionTypes = new HashSet<>();
        allowedExceptionTypes.addAll(getRetryableKafkaExceptionType());
        allowedExceptionTypes.addAll(getNonRetryableKafkaExceptionType());
        return allowedExceptionTypes;
    }

    public static Set<String> getRetryableKafkaExceptionType() {
        List<String> classes = Arrays.asList("org.apache.kafka.common.KafkaException","org.apache.kafka.common.errors.RecordTooLargeException");
        return new HashSet<>(classes);
    }

    public static Set<String> getNonRetryableKafkaExceptionType() {
        List<String> classes = Arrays.asList("org.apache.kafka.clients.consumer.InvalidOffsetException",
                "org.apache.kafka.common.errors.InvalidTopicException",
                "org.apache.kafka.common.errors.UnsupportedVersionException",
                "java.lang.IllegalStateException","org.apache.kafka.clients.consumer.CommitFailedException"
        );
        return  new HashSet<>(classes);
    }


    public static Set<String> getExceptionTypes() {
        Set<String> exceptionTypes = new HashSet<>();
        for (String exceptionType : getScyllaExceptionType()) {
            exceptionTypes.add(exceptionType.substring(exceptionType.lastIndexOf('.') + 1));
        }
        for (String exceptionType : getKafkaExceptionType()) {
            exceptionTypes.add(exceptionType.substring(exceptionType.lastIndexOf('.') + 1));
        }
        return exceptionTypes;
    }

}
