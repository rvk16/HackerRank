package com.amdocs.aia.il.common.model.providers;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.logical.LogicalSchema;
import com.amdocs.aia.repo.client.CustomElementsProvider;
import com.amdocs.aia.repo.client.ElementsProvider;

import java.util.Collections;
import java.util.List;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE;


public class CustomLogicalSchemaProvider implements CustomElementsProvider {


    @Override
    public boolean canProvide(String productKey, String elementType, Class<?> clazz) {
        return productKey.equals(ModelConstants.SHARED_PRODUCT_KEY) &&
                elementType.equals(LogicalSchema.ELEMENT_TYPE_CODE) &&
                clazz.isAssignableFrom( LogicalSchema.class);
    }

    @Override
    public <T> List<? extends T> getElements(ElementsProvider coreProvider, String productKey, String elementType, Class<T> clazz, List<? extends T> alreadyLoadedElements) {
        return (List<? extends T>)Collections.singletonList(createCacheSchema());
    }

    private LogicalSchema createCacheSchema() {
        LogicalSchema schema = new LogicalSchema();
        schema.setSchemaKey(CACHE);
        return schema;
    }
}


