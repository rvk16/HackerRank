package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.il.common.stores.AbstractQueryProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "cassandra.dialect", havingValue = "SCYLLA")
public class ScyllaQueryProvider extends AbstractQueryProvider {

}
