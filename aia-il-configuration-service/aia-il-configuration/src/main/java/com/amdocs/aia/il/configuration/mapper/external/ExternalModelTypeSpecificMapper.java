package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;

public interface ExternalModelTypeSpecificMapper {
    /**
     * The value that discriminates between different instances of the Model that is handled by this mapper
     * Valid values defined in {@link ExternalSchemaStoreTypes}
     */
    String getExternalSchemaType();

    /**
     * The value of the enum that discriminates between different instances of the DTO that is handled by this mapper
     */
    Object getDtoDiscriminatorValue();
}
