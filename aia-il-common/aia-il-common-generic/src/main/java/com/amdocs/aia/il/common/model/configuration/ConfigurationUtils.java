package com.amdocs.aia.il.common.model.configuration;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.common.model.utils.ModelUtils;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public final class ConfigurationUtils {
    private ConfigurationUtils() {
        // singleton
    }

    public static String getElementType(Class<? extends AbstractPublisherConfigurationModel> modelClass) {
        return AbstractPublisherConfigurationModel.getElementTypeFor(modelClass);
    }

    public static String getElementId(AbstractPublisherConfigurationModel model) {
        return getElementId(model.getProjectKey(), model.getClass(), model.getKey());
    }

    public static <T> boolean nullSafeContains(Set<T> items, T item){
        return item!=null && items.contains(item);
    }

    public static boolean nullSafeBoolean(Boolean b) {
        return b != null ? b : false;
    }

    public static String getElementId(String projectKey, Class modelClass, String key) {
        return ModelUtils.generateGlobalUniqueId(
                ConfigurationConstants.PRODUCT_KEY,
                projectKey,
                getElementType(modelClass),
                null,
                key);
    }

    public static OriginProcess getOriginProcess(String clientProvidedOriginProcess) {
        if (StringUtils.isEmpty(clientProvidedOriginProcess)) {
            return OriginProcess.IMPLEMENTATION; // the default value
        } else {
            final Optional<OriginProcess> originProcess = Arrays.stream(OriginProcess.values()).filter(enumValue -> enumValue.name().equals(clientProvidedOriginProcess)).findAny();
            if (originProcess.isPresent()) {
                return originProcess.get();
            } else {
                // the client provided an origin process - but is not a core process type
                return OriginProcess.CUSTOM;
            }
        }
    }

}