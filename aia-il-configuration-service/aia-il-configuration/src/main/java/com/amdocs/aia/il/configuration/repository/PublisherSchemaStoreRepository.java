package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.publisher.PublisherSchemaStore;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublisherSchemaStoreRepository extends AbstractSchemaStoreRepository<PublisherSchemaStore> {

    @Autowired
    protected PublisherSchemaStoreRepository(final AiaRepositoryOperations operations) {
        super(operations, PublisherSchemaStore.class);
    }
}