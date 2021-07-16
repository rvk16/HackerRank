package com.amdocs.aia.il.common.model.converters.external.entity;

import com.amdocs.aia.common.model.ModelElement;
import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.extensions.typesystems.*;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.il.common.model.converters.AbstractElementConverter;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractExternalEntityConverter<T extends ProjectElement> extends AbstractElementConverter<ExternalEntity,T> implements ExternalEntityConverter<T> {
    private final Map<String, AbstractTypeSystem> supportedTypeSystems;

    protected AbstractExternalEntityConverter() {
        this.supportedTypeSystems = Arrays.asList(
                new ProtoTypeSystem(),
                new SqlTypeSystem(),
                new LogicalTypeSystem(),
                new OracleTypeSystem(),
                new PostgreSqlTypeSystem())
                .stream()
                .collect(Collectors.toMap(typeSystem -> typeSystem.getName().toLowerCase(), Function.identity()));
    }

    protected String convertDatatypeFromLogical(String formattedLogicalDatatype, String targetTypeSystemName) {
        final AbstractTypeSystem typeSystem = supportedTypeSystems.get(targetTypeSystemName.toLowerCase());
        if (typeSystem == null) {
            throw new IllegalArgumentException("Unsupported type system " + targetTypeSystemName);
        }
        try {
            final Datatype logicalDatatype = LogicalTypeSystem.parse(formattedLogicalDatatype);
            return typeSystem.fromLogicalDatatype(logicalDatatype);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed parsing logical datatype " + formattedLogicalDatatype, e);
        }
    }

    protected void populateDynamicPropertyIfNotEmpty(ModelElement targetElement, String propertyKey, String value) {
        if (StringUtils.hasText(value)) {
            targetElement.setPropertyValue(propertyKey, value);
        }
    }

}
