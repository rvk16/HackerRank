package com.amdocs.aia.il.common.stores.inmemory;

import com.amdocs.aia.common.serialization.formatter.json.JsonRepeatedMessageFormatter;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.stores.RandomAccessTableFactory;
import com.amdocs.aia.il.common.stores.scylla.ScyllaDBRandomAccessTableSync;

public class InMemoryDBRandomAccessTableFactory implements RandomAccessTableFactory {

    @Override
    public RandomAccessTable create(ConfigurationRow configRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        return new InMemoryDBRandomAccessTable(configRow,jsonRepeatedMessageFormatter);
    }

    @Override
    public RandomAccessTable create(ConfigurationRow configRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter, boolean isSyncMode) {
        return new InMemoryDBRandomAccessTable(configRow,jsonRepeatedMessageFormatter);
    }

    @Override
    public RandomAccessTable create(ConfigurationRow configRow, boolean isMergeMode,JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        if (isMergeMode) {
            return new InMemoryDBRandomAccessTable(configRow,jsonRepeatedMessageFormatter);
        } else {
            return new InMemoryDBBulkRandomAccessTable(configRow,jsonRepeatedMessageFormatter);
        }
    }
}
