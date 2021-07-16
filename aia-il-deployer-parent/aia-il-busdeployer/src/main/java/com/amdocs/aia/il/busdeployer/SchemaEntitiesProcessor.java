package com.amdocs.aia.il.busdeployer;

import java.util.List;

public interface SchemaEntitiesProcessor {
    List<String> getTopicList();
    List<String> getTransformerTopicList();
    List<String> getBulkTopicList();
    List<String> getContextTransformerBulkTopicList();
}
