package com.amdocs.aia.il.common.stores.scylla;

import com.amdocs.aia.il.common.stores.AbstractQueryProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@ConditionalOnProperty(value = "cassandra.dialect", havingValue = "CASSANDRA", matchIfMissing = true)
public class CassandraQueryProvider extends AbstractQueryProvider implements Serializable {

    public static final String UPSERT_SELECT_QUERY_PREDICATES = " where tablename = ? AND tablepk = ? ;";
    public static final String UPSERT_SELECT_BULK_QUERY_PREDICATES = " where tablename = ? AND tablepk = ? LIMIT 1;";
    public static final String UPSERT_SELECT_QUERY_REF_PREDICATES = " where tablename = ? AND tablepk = ? LIMIT 1;";

    @Override
    public String getUpsertSelectQueryPredicate() {
        return UPSERT_SELECT_QUERY_PREDICATES;
    }

    @Override
    public String getUpsertSelectQueryRefPredicate() {
        return UPSERT_SELECT_QUERY_REF_PREDICATES;
    }

    @Override
    public String getUpsertSelectBulkQueryPredicate() {
        return UPSERT_SELECT_BULK_QUERY_PREDICATES;
    }

}
