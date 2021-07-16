package com.amdocs.aia.il.common.stores.inmemory;

import com.amdocs.aia.il.common.stores.QueryGenerator;
import com.amdocs.aia.il.common.stores.scylla.CassandraQueryProvider;

public class InMemoryQueryGenerator extends QueryGenerator {

    private static final long serialVersionUID = 3737057094127657791L;

    public InMemoryQueryGenerator(String schemaName,boolean isReference) {
        super(schemaName, new CassandraQueryProvider(),isReference);
    }

    @Override
    protected String prepareStatement(String schemaName, String head, String tail) {
        return head + schemaName + tail;
    }
}
