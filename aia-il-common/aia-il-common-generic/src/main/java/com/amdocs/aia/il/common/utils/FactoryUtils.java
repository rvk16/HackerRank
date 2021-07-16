package com.amdocs.aia.il.common.utils;

import com.amdocs.aia.il.common.query.QueryHandlerFactory;
import com.amdocs.aia.il.common.query.sqllite.SQLiteQueryHandlerFactory;
import com.amdocs.aia.il.common.stores.RandomAccessTableFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FactoryUtils {

    private FactoryUtils() { }

    public static Map<String, RandomAccessTableFactory> loadTableFactories() throws Exception {
        final Map<String, RandomAccessTableFactory> tableFactories = new HashMap<>();
        for (PublisherType type: PublisherType.values()) {
            tableFactories.put(type.name(), createFactoryForDataStore(type.name()));
        }
        return tableFactories;
    }

    public static <T> T createFactoryForDataStore (String type) throws Exception {
        Objects.requireNonNull(type, "Publisher type shouldn't be null");
        PublisherType publisherType = resolvePublisherType(type);
        Objects.requireNonNull(publisherType, "Invalid publisher type provided");
        return (T) Class.forName(publisherType.getFactoryClass()).newInstance();
    }

    public static QueryHandlerFactory createFactoryQueryHandler() throws Exception {
        return new SQLiteQueryHandlerFactory();
    }

    public static PublisherType resolvePublisherType(String publisherType) {
        return PublisherType.valueOf(publisherType.toUpperCase());
    }

    public enum PublisherType {

        SCYLLA("com.amdocs.aia.il.common.stores.scylla.ScyllaDBRandomAccessTableFactory"),
        INMEMORY("com.amdocs.aia.il.common.stores.inmemorycache.InMemoryCacheRandomAccessTableFactory"),
        INMEMORYDB("com.amdocs.aia.il.common.stores.inmemory.InMemoryDBRandomAccessTableFactory");

        private final String factoryClass;

        public String getFactoryClass() {
            return factoryClass;
        }

        PublisherType(String factoryClass) {
            this.factoryClass = factoryClass;
        }

    }

}
