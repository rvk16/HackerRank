package com.amdocs.aia.il.common.model.converters.cache.entity;

import com.amdocs.aia.common.model.ModelConstants;
import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.extensions.typesystems.LogicalTypeSystem;

import com.amdocs.aia.common.model.logical.LogicalAttribute;
import com.amdocs.aia.common.model.logical.LogicalEntity;

import com.amdocs.aia.il.common.model.cache.CacheAttribute;
import com.amdocs.aia.il.common.model.cache.CacheEntity;
import com.amdocs.aia.il.common.model.converters.AbstractElementConverter;


import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.ConfigurationConstants.CACHE;

public class CacheToLogicalEntityConverter<T extends ProjectElement> extends AbstractElementConverter<CacheEntity,LogicalEntity> implements CacheEntityConverter<LogicalEntity> {
    @Override
    public String getTargetProductKey() {
        return ModelConstants.SHARED_PRODUCT_KEY;
    }

    @Override
    public String getTargetElementType() {
        return LogicalEntity.ELEMENT_TYPE_CODE;
    }

    @Override
    public Class<LogicalEntity> getTargetClass() {
        return LogicalEntity.class;
    }

    @Override
    public boolean canConvert(CacheEntity cacheEntity) {
        return true;
    }


    @Override
    public LogicalEntity convert(CacheEntity cacheEntity) {
        LogicalEntity logicalEntity = super.convert(cacheEntity);
        logicalEntity.setEntityKey(cacheEntity.getEntityKey());
        logicalEntity.setDescription(cacheEntity.getDescription());
        logicalEntity.setSchemaKey(CACHE);
        logicalEntity.setAttributes(cacheEntity.getAttributes().stream().map(attribute -> convertAttribute(attribute)).collect(Collectors.toList()));
        return logicalEntity;
    }

    private LogicalAttribute convertAttribute( CacheAttribute cacheAttribute) {
        LogicalAttribute logicalAttribute = new LogicalAttribute();
        logicalAttribute.setAttributeKey(cacheAttribute.getAttributeKey());
        try {
            logicalAttribute.setDatatype(LogicalTypeSystem.parse(cacheAttribute.getDatatype()));
        }catch (ParseException e){
            throw new IllegalArgumentException("Failed parsing logical datatype " + cacheAttribute.getDatatype(), e);
        }
        logicalAttribute.setKeyPosition(cacheAttribute.getKeyPosition());

        logicalAttribute.setDescription(cacheAttribute.getDescription());
        logicalAttribute.setName(cacheAttribute.getName());
        logicalAttribute.setOrigin(cacheAttribute.getOrigin());
        logicalAttribute.setProperties(cacheAttribute.getProperties());
        return logicalAttribute;
    }


}
