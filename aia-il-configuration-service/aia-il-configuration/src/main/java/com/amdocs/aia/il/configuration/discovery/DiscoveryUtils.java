package com.amdocs.aia.il.configuration.discovery;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import com.amdocs.aia.common.model.logical.PrimitiveDatatypeConstraint;
import com.amdocs.aia.il.configuration.discovery.annotations.DiscoveryParameter;
import com.amdocs.aia.il.configuration.dto.SchemaDiscoveryRequestDTO;
import com.amdocs.aia.il.configuration.exception.ApiException;
import com.amdocs.aia.il.configuration.message.AiaApiMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import io.swagger.models.properties.*;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class DiscoveryUtils {
    private static final Datatype DEFAULT_DECIMAL_DATATYPE = Datatype.forPrimitive(
            PrimitiveDatatype.DECIMAL, ImmutableMap.of(
                    PrimitiveDatatypeConstraint.PRECISION, 15,
                    PrimitiveDatatypeConstraint.SCALE, 4));


    // we can safely create a single instance since the conversion service is thread safe.
    // see https://www.logicbig.com/tutorials/spring-framework/spring-core/conversion-service.html
    private static final ConversionService CONVERSION_SERVICE = new DefaultConversionService();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DiscoveryUtils() {
    }

    public static List<Field> findAnnotatedFields(Class<?> type, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            fields.addAll(Arrays.stream(c.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotationClass)).collect(Collectors.toList()));
        }
        return fields;
    }

    /**
     * Converts a string to Title Case
     */
    public static String getNameFromKey(String key) {
        return WordUtils.capitalizeFully(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(key), '_'), '_', '-', ' ')
                .replaceAll("[_-]", " ")
                .replaceAll("\\s{2,}", " ");
    }

    public static <T extends ExternalModelDiscoveryParameters> T buildTypedDiscoveryParameters(Map<String, Object> paramsMap, Class<T> paramsClass) {
        // note that we set here field-by-field instead of using something like ObjectMapper.convertValue, because we want to get field-specific errors in case of invalid parameter values
        T typedParameters = constructObject(paramsClass);
        List<String> missingRequiredParameters = new ArrayList<>();
        findAnnotatedFields(paramsClass, DiscoveryParameter.class).forEach(field -> {
            final DiscoveryParameter annotation = field.getDeclaredAnnotation(DiscoveryParameter.class);
            String paramName = getDiscoveryParamName(field, annotation);
            Object paramValue = paramsMap.get(paramName);
            if (paramValue != null && !paramValue.equals("")) { // we treat empty strings as empty
                Object convertedValue = convertParameterValue(paramName, paramValue, field.getType());
                try {
                    PropertyUtils.setProperty(typedParameters, field.getName(), convertedValue);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot set value " + convertedValue + " on target field " + paramsClass + "." + field.getName());
                }
            } else if (annotation.required()) {
                missingRequiredParameters.add(paramName);
            }
        });
        if (!missingRequiredParameters.isEmpty()) {
            throw new ApiException(
                    AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                    AiaApiMessages.GENERAL.DISCOVERY_MISSING_REQUIRED_PARAMETERS,
                    missingRequiredParameters.stream().collect(Collectors.joining(",")));
        }
        return typedParameters;
    }

    public static String discoveryRequestToJson(SchemaDiscoveryRequestDTO request) {
        try {
            return OBJECT_MAPPER.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed formatting discovery request object", e);
        }
    }

    public static Map<String, Object> convertDiscoveryParametersToMap(ExternalModelDiscoveryParameters parameters) {
        return findAnnotatedFields(parameters.getClass(), DiscoveryParameter.class).stream()
                .map(field -> new AbstractMap.SimpleImmutableEntry<>(getDiscoveryParamName(field), getDiscoveryParamValue(parameters, field)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static Object getDiscoveryParamValue(ExternalModelDiscoveryParameters parameters, Field field) {
        try {
            return PropertyUtils.getProperty(parameters, field.getName());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot get value for parameter field " + field.getName() + " of class " + parameters.getClass(), e);
        }
    }

    private static String getDiscoveryParamName(Field field) {
        return getDiscoveryParamName(field, field.getDeclaredAnnotation(DiscoveryParameter.class));
    }

    private static String getDiscoveryParamName(Field field, DiscoveryParameter annotation) {
        return StringUtils.isEmpty(annotation.name()) ? field.getName() : annotation.name();
    }

    private static Object convertParameterValue(String paramName, Object value, Class<?> targetType) {
        try {
            return CONVERSION_SERVICE.convert(value, targetType);
        } catch (Exception e) {
            throw new ApiException(
                    AiaApiException.AiaApiHttpCodes.BAD_RERQUEST,
                    AiaApiMessages.GENERAL.DISCOVERY_INVALID_PARAMETER_VALUE,
                    paramName,
                    String.valueOf(value),
                    targetType.getSimpleName());
        }
    }

    private static <T> T constructObject(Class<T> paramsClass) {
        try {
            return paramsClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("No default constructor found for class " + paramsClass.getName());
        }
    }

    // Note that for now we haven't implemented a type system, but instead we simple have a method that is used upon discovery
    // see https://swagger.io/specification/v2/
    public static String openApiV2DatatypeToLogical(final String type, String format) {
        Datatype logicalDatatype;
        if (BaseIntegerProperty.isType(type, format)) {
            if (LongProperty.isType(type, format)) {
                logicalDatatype = Datatype.forPrimitive(PrimitiveDatatype.LONG);
            } else {
                logicalDatatype = Datatype.forPrimitive(PrimitiveDatatype.INTEGER);
            }
        } else if (DecimalProperty.isType(type, format)) {
            if (FloatProperty.isType(type, format)) {
                logicalDatatype = DEFAULT_DECIMAL_DATATYPE; // for backward compatibility with ADH
            } else {
                logicalDatatype = Datatype.forPrimitive(PrimitiveDatatype.DOUBLE);
            }
        } else if (BooleanProperty.isType(type, format)) {
            logicalDatatype = Datatype.forPrimitive(PrimitiveDatatype.BOOLEAN);
        } else if (DateProperty.isType(type, format) || DateTimeProperty.isType(type, format)) {
            logicalDatatype = Datatype.forPrimitive(PrimitiveDatatype.TIMESTAMP);
        } else {
            // we'll assume that all the rest are strings
            logicalDatatype = Datatype.forPrimitive(PrimitiveDatatype.STRING);
        }
        return LogicalTypeSystem.format(logicalDatatype);
    }
}
