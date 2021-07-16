package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.common.serialization.formatter.json.JsonRepeatedMessageFormatter;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;
import com.amdocs.aia.il.common.stores.RandomAccessTable;
import com.amdocs.aia.il.common.stores.RandomAccessTableFactory;


public class ScyllaDBRandomAccessTableFactory implements RandomAccessTableFactory {

    @Override
    public RandomAccessTable create(ConfigurationRow configRow,JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        return new ScyllaDBRandomAccessTable(configRow,jsonRepeatedMessageFormatter);
    }

    @Override
    public RandomAccessTable create(ConfigurationRow configRow, JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter, boolean isSyncMode) {
        return new ScyllaDBRandomAccessTableSync(configRow,jsonRepeatedMessageFormatter);
    }

    @Override
    public RandomAccessTable create(ConfigurationRow configRow, boolean isMergeMode,JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter) {
        if (isMergeMode) {
            return new ScyllaDBRandomAccessTable(configRow,jsonRepeatedMessageFormatter);
        } else {
            return new ScyllaDBBulkRandomAccessTable(configRow,jsonRepeatedMessageFormatter);
        }
    }

}
