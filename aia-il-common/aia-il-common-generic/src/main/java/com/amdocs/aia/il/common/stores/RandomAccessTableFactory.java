package com.amdocs.aia.il.common.stores;

import com.amdocs.aia.common.serialization.formatter.json.JsonRepeatedMessageFormatter;
import com.amdocs.aia.il.common.model.configuration.tables.ConfigurationRow;


public interface RandomAccessTableFactory {

    RandomAccessTable create(ConfigurationRow configRow,boolean isMergeMode,JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter);

    RandomAccessTable create(ConfigurationRow configRow,JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter);

    RandomAccessTable create(ConfigurationRow configRow,JsonRepeatedMessageFormatter jsonRepeatedMessageFormatter, boolean isSyncMode);

}
