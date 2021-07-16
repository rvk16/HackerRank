package com.amdocs.aia.il.common.model.converters;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.il.common.model.ConfigurationConstants;

public abstract class AbstractElementConverter<S extends ProjectElement,T extends ProjectElement> implements ElementConverter<S,T> {

    @Override
    public T convert(S source) {
        final Class<T> targetClass = getTargetClass();
        final T target = ConverterUtils.createAndCopyProperties(targetClass, source, "elementType", "id");
        target.setOrigin(ConfigurationConstants.DYNAMICALLY_PROVIDED_ORIGIN);
        target.setProductKey(getTargetProductKey());
        return target;
    }
}
