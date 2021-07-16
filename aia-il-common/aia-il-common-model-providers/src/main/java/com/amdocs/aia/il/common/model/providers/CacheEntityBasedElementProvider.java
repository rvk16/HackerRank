package com.amdocs.aia.il.common.model.providers;



import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.common.model.converters.ElementConverter;
import com.amdocs.aia.il.common.model.converters.cache.entity.CacheEntityConverter;

import com.amdocs.aia.repo.client.ElementsProvider;

import java.util.List;


public class CacheEntityBasedElementProvider extends AbstractConversionBasedElementsProvider<CacheEntity> {



    @Override
    protected List<CacheEntity> loadSourceElements(ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, CacheEntity.ELEMENT_TYPE, CacheEntity.class);
    }

    @Override
    protected Class<? extends ElementConverter> getConverterServiceInterface() {
        return CacheEntityConverter.class;
    }
}
