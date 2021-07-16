package com.amdocs.aia.il.deployer;

import org.springframework.stereotype.Component;

import javax.inject.Provider;

@Component
public class SourceTableDeployerProvider {
    private final Provider<SourceTableDeployer> provider;

    public SourceTableDeployerProvider(final Provider<SourceTableDeployer> provider) {
        this.provider = provider;
    }

    public SourceTableDeployer getTableDeployer(final String tableName) {
        final SourceTableDeployer tableDeployer = provider.get();
        tableDeployer.setSchemaName(tableName);
        return tableDeployer;
    }
}