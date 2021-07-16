package com.amdocs.aia.il.common.model.converters.shared.schema;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.common.model.converters.AbstractElementConverter;


public abstract class AbstractTargetSchemaConverter<T extends ProjectElement> extends AbstractElementConverter<SchemaStore,T> implements TargetSchemaConverter<T> {
}
