package com.amdocs.aia.il.common.model.providers;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.store.EntityStore;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerEntityStoreModel;
import com.amdocs.aia.il.common.model.converters.ElementConverter;
import com.amdocs.aia.il.common.model.converters.shared.entity.TargetEntityConverter;
import com.amdocs.aia.repo.client.ElementsProvider;

import java.util.List;


public class EntityStoreBasedElementProvider extends AbstractConversionBasedElementsProvider<EntityStore> {



    @Override
    protected List<EntityStore> loadSourceElements(ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ModelConstants.SHARED_PRODUCT_KEY, EntityStore.ELEMENT_TYPE_CODE, EntityStore.class);
    }

    @Override
    protected Class<? extends ElementConverter> getConverterServiceInterface() {
        return TargetEntityConverter.class;
    }


    @Override
    protected boolean needsConversion(EntityStore sourceElement, ElementConverter<EntityStore, ?> converter, List<?> alreadyLoadedElements) {
        if (AbstractIntegrationLayerEntityStoreModel.class.isAssignableFrom(converter.getTargetClass())) {
            return !alreadyLoadedElements.stream()
                    .filter(elem -> elem instanceof AbstractIntegrationLayerEntityStoreModel)
                    .map(AbstractIntegrationLayerEntityStoreModel.class::cast)
                    .filter(privateEntityStore ->
                            privateEntityStore.getSchemaStoreKey().equals(sourceElement.getSchemaStoreKey()) &&
                                    privateEntityStore.getEntityStoreKey().equals(sourceElement.getEntityStoreKey()))
                    .findAny()
                    .isPresent();
        }
        else {
            return true;
        }
    }
}
