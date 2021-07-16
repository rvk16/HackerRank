package com.amdocs.aia.il.common.model.configuration.tables;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.repo.annotations.RepoElementStatus;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;

import java.util.List;

/**
 * Created by SWARNIMJ
 */
public abstract class AbstractPublisherConfigurationModel extends ProjectElement {
    private static final long serialVersionUID = -5764212896211085590L;

    @RepoSearchable
    private String key;

    @RepoSearchable
    private String publisherName;

    @RepoElementStatus
    @RepoTransient
    private ChangeStatus status;

    @RepoTransient
    private transient List<ElementDependency> dependencies;

    @RepoTransient
    private transient List<ElementPublicFeature> publicFeatures;

    public AbstractPublisherConfigurationModel() {
        super.setElementType(getClass().getSimpleName());
    }

    public static String getElementTypeFor(Class<? extends AbstractPublisherConfigurationModel> modelClass) {
        return modelClass.getSimpleName();
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ChangeStatus getStatus() {
        return status;
    }

    public void setStatus(ChangeStatus status) {
        this.status = status;
    }

    @Override
    public List<ElementDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<ElementDependency> dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public List<ElementPublicFeature> getPublicFeatures() {
        return publicFeatures;
    }

    public void setPublicFeatures(List<ElementPublicFeature> publicFeatures) {
        this.publicFeatures = publicFeatures;
    }
}