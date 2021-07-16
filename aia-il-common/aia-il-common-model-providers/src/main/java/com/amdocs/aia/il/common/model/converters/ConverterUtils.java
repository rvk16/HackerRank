package com.amdocs.aia.il.common.model.converters;

import com.amdocs.aia.common.model.store.SharedStores;
import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;
import org.springframework.beans.BeanUtils;

public class ConverterUtils {
    public static String getDataChannelSchemaStoreKey(String schemaKey) {
        return schemaKey + "DataChannel";
    }

    public static String getPublisherStoreSchemaStoreKey(String schemaKey) {
        return schemaKey + "PublisherStore";
    }

    public static <T, S> T createAndCopyProperties(Class<T> targetClass, Object source, String... ignoreProperties) {
        try {
            final T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target, ignoreProperties);
            return target;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSharedStoreType(PhysicalStoreType physicalStoreType) {
        switch(physicalStoreType) {
            case CSV:
                return SharedStores.FileStore.STORE_TYPE;
            case SQL:
                return SharedStores.RDBMS.STORE_TYPE;
            case KAFKA:
                // currently since there no physical shared store is created for Kafka schemas
            default:
                return null;
        }
    }
}
