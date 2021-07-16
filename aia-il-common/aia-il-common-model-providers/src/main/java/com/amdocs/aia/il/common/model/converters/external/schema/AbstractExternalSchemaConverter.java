package com.amdocs.aia.il.common.model.converters.external.schema;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.il.common.model.converters.AbstractElementConverter;
import com.amdocs.aia.il.common.model.external.ExternalSchema;

public abstract class AbstractExternalSchemaConverter<T extends ProjectElement> extends AbstractElementConverter<ExternalSchema,T> implements ExternalSchemaConverter<T> {
}
