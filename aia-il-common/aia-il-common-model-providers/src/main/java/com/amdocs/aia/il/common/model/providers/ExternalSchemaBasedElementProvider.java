package com.amdocs.aia.il.common.model.providers;

import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.converters.ElementConverter;
import com.amdocs.aia.il.common.model.converters.external.schema.ExternalSchemaConverter;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.repo.client.ElementsProvider;

import java.util.List;

/**
 * A custom elements provider (for il runtime components) that dynamically provides STORES (e.g. private DataChannel stores, shared DataChannel stores,
 * physical stores, etc.) based on ExternalSchema elements (which are persisted in the configuration by il authoring).
 *
 * The architectural concept behind this provider is that authoring activities (performed either by implementation users via authoring ui or by mapping sheet migration
 * process) only persist ExternalSchema and ExternalEntity to represent source entities, and any internal store that internally maintained by the integration layer
 * (e.g. the datachannel store representing kafka 1, or the publisher store used by the replicator) are created and provided at runtime (i.e. they are not persistent
 * in the configuration). So this custom provider knows how to convert external schema into any derived schema store store.
 *
 * Note: this element provider currently loads and CACHES External schemas (in a lazy manner - i.e. when first needed), so it is not possible
 * to change the configuration after the provider was first used (this is done for performance optimization, in order to avoid multiple loads of the same configuration
 * file when different stores are requested). In any case. changing configuration at runtime is not supported and not needed (neither by aia-repo framework nor by
 * il runtime components). In any case, if in the future there is a need to dynamically change the configuration, this caching can be removed.
 */
public class ExternalSchemaBasedElementProvider extends AbstractConversionBasedElementsProvider<ExternalSchema> {

    @Override
    protected List<ExternalSchema> loadSourceElements(ElementsProvider elementsProvider) {
        return elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, ExternalSchema.ELEMENT_TYPE, ExternalSchema.class);
    }

    @Override
    protected Class<? extends ElementConverter> getConverterServiceInterface() {
        return ExternalSchemaConverter.class;
    }
}
