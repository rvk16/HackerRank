package com.amdocs.aia.il.common.stores.inmemorycache;

import com.amdocs.aia.common.serialization.formatter.json.JsonRepeatedMessageFormatter;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.stores.RandomAccessTableFactory;
import com.amdocs.aia.il.common.stores.scylla.ScyllaDBRandomAccessTableSync;

import java.io.Serializable;

public class InMemoryCacheRandomAccessTableFactory implements RandomAccessTableFactory, Serializable {

    private static final long serialVersionUID = -1538762670610088684L;

    @Override
    public RandomAccessTable create(ConfigurationRow configRow,JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        return new InMemoryCacheRandomAccessTable(configRow);
    }

    @Override
    public RandomAccessTable create(ConfigurationRow configRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter, boolean isSyncMode) {
        return new InMemoryCacheRandomAccessTable(configRow);
    }

    @Override
    public RandomAccessTable create(ConfigurationRow configRow, boolean isMergeMode, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        if (isMergeMode) {
            return new InMemoryCacheRandomAccessTable(configRow);
        } else {
            return new InMemoryCacheBulkRandomAccessTable(configRow);
        }
    }

}
