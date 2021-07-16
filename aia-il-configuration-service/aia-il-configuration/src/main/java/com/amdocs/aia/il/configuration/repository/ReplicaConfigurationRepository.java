package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.configuration.tables.ReplicaStoreConfiguration;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ReplicaConfigurationRepository extends AbstractConfigurationRepository<ReplicaStoreConfiguration> {

    @Autowired
    protected ReplicaConfigurationRepository(final AiaRepositoryOperations operations) {
        super(operations, ReplicaStoreConfiguration.class);
    }
}