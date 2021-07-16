package com.amdocs.aia.il.configuration.mapper.external;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class ExternalModelMappersConfiguration {
    private final ListableBeanFactory beanFactory;

    @Inject
    public ExternalModelMappersConfiguration(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public ExternalModelTypeSpecificMapperLookup<ExternalSchemaStoreInfoMapper> externalSchemaStoreInfoMapperLookup() {
        return new ExternalModelTypeSpecificMapperLookup(beanFactory.getBeansOfType(ExternalSchemaStoreInfoMapper.class).values());
    }

    @Bean
    public ExternalModelTypeSpecificMapperLookup<ExternalEntityStoreInfoMapper> externalEntityStoreInfoMapperLookup() {
        return new ExternalModelTypeSpecificMapperLookup(beanFactory.getBeansOfType(ExternalEntityStoreInfoMapper.class).values());
    }

    @Bean
    public ExternalModelTypeSpecificMapperLookup<ExternalAttributeStoreInfoMapper> externalAttributeStoreInfoMapperLookup() {
        return new ExternalModelTypeSpecificMapperLookup(beanFactory.getBeansOfType(ExternalAttributeStoreInfoMapper.class).values());
    }

    @Bean
    public ExternalModelTypeSpecificMapperLookup<ExternalSchemaCollectionRulesMapper> externalSchemaCollectionRulesMapperLookup() {
        return new ExternalModelTypeSpecificMapperLookup(beanFactory.getBeansOfType(ExternalSchemaCollectionRulesMapper.class).values());
    }

    @Bean
    public ExternalModelTypeSpecificMapperLookup<ExternalEntityCollectionRulesMapper> externalEntityCollectionRulesMapperLookup() {
        return new ExternalModelTypeSpecificMapperLookup(beanFactory.getBeansOfType(ExternalEntityCollectionRulesMapper.class).values());
    }

}
