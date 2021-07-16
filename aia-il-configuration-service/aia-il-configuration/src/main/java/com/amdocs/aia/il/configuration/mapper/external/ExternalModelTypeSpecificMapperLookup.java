package com.amdocs.aia.il.configuration.mapper.external;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExternalModelTypeSpecificMapperLookup<T extends ExternalModelTypeSpecificMapper> {

    private final Map<String, ? extends T> mapperBySchemaType;
    private final Map<Object, ? extends T> mapperByDtoDiscriminator;

    public ExternalModelTypeSpecificMapperLookup(Collection<? extends T> mappers) {
        mapperBySchemaType = mappers.stream().collect(Collectors.toMap(ExternalModelTypeSpecificMapper::getExternalSchemaType, Function.identity()));
        mapperByDtoDiscriminator = mappers.stream().collect(Collectors.toMap(ExternalModelTypeSpecificMapper::getDtoDiscriminatorValue, Function.identity()));
    }

    public T getBySchemaType(String externalSchemaType) {
        final T mapper = mapperBySchemaType.get(externalSchemaType);
        if (mapper == null) {
            throw new IllegalArgumentException("No mappers registered for external schema type " + externalSchemaType);
        }
        return mapper;
    }

    public T getByDtoDiscriminator(Object discriminatorValue) {
        final T mapper = mapperByDtoDiscriminator.get(discriminatorValue);
        if (mapper == null) {
            throw new IllegalArgumentException("No mappers registered for dto discriminator value " + discriminatorValue);
        }
        return mapper;
    }

}
