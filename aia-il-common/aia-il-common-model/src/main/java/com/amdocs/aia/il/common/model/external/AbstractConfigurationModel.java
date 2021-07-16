package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.ElementPublicFeature;
import com.amdocs.aia.common.model.repo.annotations.RepoElementStatus;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;

import java.util.List;

public class AbstractConfigurationModel extends ProjectElement {

    @RepoSearchable
    private String createdBy;

    private long createdAt;

    @RepoElementStatus
    @RepoTransient
    private ChangeStatus changeStatus;

    @RepoTransient
    private transient List<ElementDependency> dependencies;

    @RepoTransient
    private transient List<ElementPublicFeature> publicFeatures;


    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
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

    public ChangeStatus getChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(ChangeStatus changeStatus) {
        this.changeStatus = changeStatus;
    }

}
