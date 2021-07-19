package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.configuration.discovery.AbstractDiscovererProvider;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ExternalSqlDiscovererProvider extends AbstractDiscovererProvider<ExternalSqlDiscoverer> {

    @Override
    public Set<ExternalSchemaType> getSupportedExternalSchemaTypes() {
        return Sets.immutableEnumSet(ExternalSchemaType.ORACLE);
    }
}
