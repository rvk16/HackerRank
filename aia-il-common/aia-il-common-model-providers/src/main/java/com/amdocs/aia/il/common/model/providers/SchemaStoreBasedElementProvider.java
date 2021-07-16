package com.amdocs.aia.il.common.model.providers;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.store.SchemaStore;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.converters.ElementConverter;

import com.amdocs.aia.il.common.model.converters.shared.schema.SchemaStoreToPrivateTargetDataChannelSchemaStoreConverter;
import com.amdocs.aia.il.common.model.converters.shared.schema.TargetSchemaConverter;
import com.amdocs.aia.repo.client.ElementsProvider;


import java.util.List;


public class SchemaStoreBasedElementProvider extends AbstractConversionBasedElementsProvider<SchemaStore> {


    @Override
    protected List<SchemaStore> loadSourceElements(ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ModelConstants.SHARED_PRODUCT_KEY, SchemaStore.ELEMENT_TYPE_CODE , SchemaStore.class);
    }

    @Override
    protected Class<? extends ElementConverter> getConverterServiceInterface() {
        return TargetSchemaConverter.class;
    }

    @Override
    protected boolean needsConversion(SchemaStore sourceElement, ElementConverter<SchemaStore, ?> converter, List<?> alreadyLoadedElements) {
        if (AbstractIntegrationLayerSchemaStoreModel.class.isAssignableFrom(converter.getTargetClass())) {
            return !alreadyLoadedElements.stream()
                    .filter(elem -> elem instanceof AbstractIntegrationLayerSchemaStoreModel)
                    .map(AbstractIntegrationLayerSchemaStoreModel.class::cast)
                    .filter(privateSchemaStore -> privateSchemaStore.getSchemaStoreKey().equals(sourceElement.getSchemaStoreKey()))
                    .findAny()
                    .isPresent();
        }
        else {
            return true;
        }
    }

}
