package com.amdocs.aia.il.common.model;

import com.amdocs.aia.common.model.ProjectElement;
import com.amdocs.aia.common.model.repo.ElementDependency;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.repo.annotations.RepoTransient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityReferentialIntegrity extends ProjectElement {

    private static final long serialVersionUID = -5947208761659372401L;

    @RepoSearchable
    @NotNull
    @Pattern(regexp = "^[a-zA-Z_0-9]+$")
    private String elementKey;

    @NotNull
    @RepoSearchable
    private String logicalSchemaKey;

    @NotNull
    @RepoSearchable
    private String logicalEntityKey;

    private List<Relation> relations;

    @RepoTransient
    private transient List<ElementDependency> dependencies;

    public EntityReferentialIntegrity() {//NOSONAR
        super.setElementType(EntityReferentialIntegrity.class.getSimpleName());
        super.setProductKey(ConfigurationConstants.PRODUCT_KEY);
    }

    public EntityReferentialIntegrity projectKey(final String projectKey) {
        setProjectKey(projectKey);
        return this;
    }

    @Override
    public String getId() {
        return String.format("%s_%s_%s_%s", getProductKey(), getProjectKey(), getElementType(), getElementKey());
    }

    public String getLogicalSchemaKey() {
        return logicalSchemaKey;
    }

    public EntityReferentialIntegrity setLogicalSchemaKey(String logicalSchemaKey) {
        this.logicalSchemaKey = logicalSchemaKey;
        return this;
    }

    public String getLogicalEntityKey() {
        return logicalEntityKey;
    }

    public EntityReferentialIntegrity setLogicalEntityKey(String logicalEntityKey) {
        this.logicalEntityKey = logicalEntityKey;
        return this;
    }

    public List<Relation> getRelations() {
        return relations;
    }

    public EntityReferentialIntegrity addRelationsItem(Relation relationsItem) {
        if (this.relations == null) {
            this.relations = new ArrayList<>();
        }
        this.relations.add(relationsItem);
        return this;
    }

    public EntityReferentialIntegrity setRelations(List<Relation> relations) {
        this.relations = new ArrayList<>(relations);
        return this;
    }

    @Override
    public List<ElementDependency> getDependencies() {
        return new ArrayList<>(dependencies);
    }

    public void setDependencies(List<ElementDependency> dependencies) {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList<>(dependencies);
        } else {
            Collections.copy(this.dependencies, dependencies);
        }
    }

    public String getElementKey() {
        return String.format(logicalSchemaKey + "_" + logicalEntityKey).replaceAll("\\s+", "");
    }

    public void clearDependencies() {
        if (this.dependencies != null) {
            this.dependencies.clear();
            this.dependencies = null;
        }
    }
}
