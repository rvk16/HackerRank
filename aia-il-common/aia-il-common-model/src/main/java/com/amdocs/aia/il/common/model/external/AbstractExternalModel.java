package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.common.model.repo.annotations.RepoElementStatus;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;

public abstract class AbstractExternalModel extends AbstractConfigurationModel {

    private static final long serialVersionUID = 6519643281841946279L;

    @RepoElementStatus
    @RepoTransient
    private ChangeStatus changeStatus;

    @RepoSearchable
    private boolean isActive;


    public boolean getIsActive() {
        return isActive;
    }


    public void setIsActive(boolean active) {
        isActive = active;
    }


}
