package com.amdocs.aia.il.common.model.providers;

import com.amdocs.aia.il.common.model.converters.ElementConverter;
import com.amdocs.aia.repo.client.CustomElementsProvider;
import com.amdocs.aia.repo.client.ElementsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A custom elements provider that uses one or more one-to-one conversions from given source element type to dynamically requested target types
 */
public abstract class AbstractConversionBasedElementsProvider<S> implements CustomElementsProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConversionBasedElementsProvider.class);

    private List<S> sourceElements;
    private List<ElementConverter<S, ?>> converters = new ArrayList<>();

    protected AbstractConversionBasedElementsProvider() {
        ServiceLoader.load(getConverterServiceInterface()).forEach(this::registerConverter);
    }

    private void registerConverter(ElementConverter elementConverter) {
        LOGGER.info("registering element converter of type (elementConverter:{},targetProductKey:{}, targetElementType:{}, targetClass:{})", elementConverter.getClass().getSimpleName(), elementConverter.getTargetProductKey(), elementConverter.getTargetElementType(), elementConverter.getTargetClass().getSimpleName());
        converters.add(elementConverter);
    }

    @Override
    public final boolean canProvide(String productKey, String elementType, Class<?> clazz) {
        return converters.stream().anyMatch(converter -> canProvide(converter, productKey, elementType, clazz));
    }

    @Override
    public final <T> List<? extends T> getElements(ElementsProvider coreProvider, String productKey, String elementType, Class<T> clazz, List<? extends T> alreadyLoadedElements) {
        if (sourceElements == null) {
            sourceElements = loadSourceElements(coreProvider);
        }
        final List<? extends T> elements = (List<? extends T>) converters.stream()
                .filter(converter -> canProvide(converter, productKey, elementType, clazz))
                .flatMap(converter -> sourceElements.stream()
                        .filter(converter::canConvert)
                        .filter(sourceElement -> needsConversion(sourceElement, converter, alreadyLoadedElements))
                        .map(sourceElement -> convertElement(coreProvider, converter, sourceElement)))
                .collect(Collectors.toList());
        return elements;
    }

    private Object convertElement(ElementsProvider coreProvider, ElementConverter<S, ?> converter, S sourceElement) {
        Object targetElement = converter.convert(sourceElement);
        enrichConvertedElement(coreProvider, sourceElement, targetElement);
        return targetElement;
    }

    // can be overridden
    protected <T> void enrichConvertedElement(ElementsProvider coreProvider, S sourceElement, T targetElement) {
    }

    public List<ElementConverter<S, ?>> getConverters() {
        return converters;
    }

    private boolean canProvide(ElementConverter<S, ?> converter, String productKey, String elementType, Class<?> clazz) {
        return productKey.equals(converter.getTargetProductKey()) &&
                elementType.equals(converter.getTargetElementType()) &&
                clazz.isAssignableFrom(converter.getTargetClass());
    }

    /**
     * CAn be overridden. Note that this method is called only for providers that can actually convert this source element
     */
    protected boolean needsConversion(S sourceElement, ElementConverter<S, ?> converter, List<?> alreadyLoadedElements) {
        return true;
    }

    protected abstract List<S> loadSourceElements(ElementsProvider elementsProvider);

    protected abstract Class<? extends ElementConverter> getConverterServiceInterface();
}
