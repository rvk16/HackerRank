package com.amdocs.aia.il.common.model.converters.shared.entity;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.extensions.typesystems.*;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.il.common.model.converters.AbstractElementConverter;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractTargetEntityConverter<T extends ProjectElement> extends AbstractElementConverter<EntityStore,T> implements TargetEntityConverter<T> {
    private final Map<String, AbstractTypeSystem> supportedTypeSystems;

    protected AbstractTargetEntityConverter() {
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

}
