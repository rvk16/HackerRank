package com.amdocs.aia.il.configuration.repository;

import com.amdocs.aia.il.common.model.publisher.PublisherEntityStore;
import com.amdocs.aia.repo.client.AiaRepositoryOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublisherEntityStoreRepository extends AbstractEntityStoreRepository<PublisherEntityStore> {

    @Autowired
    public PublisherEntityStoreRepository(final AiaRepositoryOperations operations) {
        super(operations, PublisherEntityStore.class);
    }
}