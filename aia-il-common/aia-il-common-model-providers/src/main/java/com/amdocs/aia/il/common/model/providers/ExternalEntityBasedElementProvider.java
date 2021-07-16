package com.amdocs.aia.il.common.model.providers;

import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;
import com.amdocs.aia.common.model.logical.Datatype;
import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.converters.ElementConverter;
import com.amdocs.aia.il.common.model.converters.external.entity.ExternalEntityConverter;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.kafka.ExternalKafkaSchemaStoreInfo;
import com.amdocs.aia.il.common.model.physical.kafka.KafkaEntityStore;
import com.amdocs.aia.repo.client.ElementsProvider;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A custom elements provider (for il runtime components) that dynamically provides STORES (e.g. private DataChannel stores, shared DataChannel stores,
 * physical stores, etc.) based on ExternalEntity elements (which are persisted in the configuration by il authoring).
 * <p>
 * The architectural concept behind this provider is that authoring activities (performed either by implementation users via authoring ui or by mapping sheet migration
 * process) only persist ExternalSchema and ExternalEntity to represent source entities, and any internal store that internally maintained by the integration layer
 * (e.g. the datachannel store representing kafka 1, or the publisher store used by the replicator) are created and provided at runtime (i.e. they are not persistent
 * in the configuration). So this custom provider knows how to convert external entity into any derived entity store store.
 * <p>
 * Note: this element provider currently loads and CACHES External entities (in a lazy manner - i.e. when first needed), so it is not possible
 * to change the configuration after the provider was first used (this is done for performance optimization, in order to avoid multiple loads of the same configuration
 * file when different stores are requested). In any case. changing configuration at runtime is not supported and not needed (neither by aia-repo framework nor by
 * il runtime components). In any case, if in the future there is a need to dynamically change the configuration, this caching can be removed.
 */
public class ExternalEntityBasedElementProvider extends AbstractConversionBasedElementsProvider<ExternalEntity> {

    private static final String TIMESTAMP_DATATYPE = LogicalTypeSystem.format(Datatype.forPrimitive(PrimitiveDatatype.TIMESTAMP));

    private Map<String, ExternalSchema> externalSchemaByKey;

    @Override
    protected List<ExternalEntity> loadSourceElements(ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, ExternalEntity.ELEMENT_TYPE, ExternalEntity.class);
    }

    @Override
    protected Class<? extends ElementConverter> getConverterServiceInterface() {
        return ExternalEntityConverter.class;
    }

    private void lazyInitSchemas(ElementsProvider elementsProvider) {
        if (externalSchemaByKey == null) {
            externalSchemaByKey =
                    elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, ExternalSchema.ELEMENT_TYPE, ExternalSchema.class)
                    .stream()
                    .collect(Collectors.toMap(ExternalSchema::getSchemaKey, Function.identity()));
        }
    }

    private ExternalSchema getSchemaFor(ExternalEntity externalEntity) {
        return externalSchemaByKey.get(externalEntity.getSchemaKey());
    }

    @Override
    protected <T> void enrichConvertedElement(ElementsProvider coreProvider, ExternalEntity sourceElement, T targetElement) {
        lazyInitSchemas(coreProvider);
        if (KafkaEntityStore.class.isInstance(targetElement)) {
            enrichKafkaEntityStore(sourceElement, (KafkaEntityStore)targetElement);
        }
    }

    private void enrichKafkaEntityStore(ExternalEntity externalEntity, KafkaEntityStore kafkaEntityStore) {
        final ExternalSchema externalSchema = getSchemaFor(externalEntity);
        String defaultDateFormat = externalSchema != null && externalSchema.getStoreInfo() instanceof ExternalKafkaSchemaStoreInfo ?
                ((ExternalKafkaSchemaStoreInfo)externalSchema.getStoreInfo()).getDefaultDateFormat() : null;
        if (StringUtils.hasText(defaultDateFormat)) {
            kafkaEntityStore.getAttributeStores().stream()
                    .filter(attribute -> TIMESTAMP_DATATYPE.equals(attribute.getType())) // we assume that kafka entity store is using the logical type system
                    .filter(attribute -> !attribute.hasProperty(KafkaEntityStore.DATE_FORMAT))
                    .forEach(attribute -> attribute.setPropertyValue(KafkaEntityStore.DATE_FORMAT, defaultDateFormat));
        }
    }
}
