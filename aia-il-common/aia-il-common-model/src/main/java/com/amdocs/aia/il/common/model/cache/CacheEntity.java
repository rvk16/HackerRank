package com.amdocs.aia.il.common.model.cache;

import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.external.AbstractConfigurationModel;

import java.util.List;

public class CacheEntity extends AbstractConfigurationModel {

    public static final String ELEMENT_TYPE = CacheEntity.class.getSimpleName();
    private static final long serialVersionUID = 5908143132244664470L;

    @RepoSearchable
    private String entityKey;
    private List<CacheAttribute> attributes;

    public CacheEntity(){
        setElementType(ELEMENT_TYPE);
        setProductKey(ConfigurationConstants.PRODUCT_KEY);
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public List<CacheAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CacheAttribute> attributes) {
        this.attributes = attributes;
    }
}
