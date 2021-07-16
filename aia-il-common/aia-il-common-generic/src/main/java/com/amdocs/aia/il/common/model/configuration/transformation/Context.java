package com.amdocs.aia.il.common.model.configuration.transformation;

import com.amdocs.aia.common.model.DerivedElement;
import com.amdocs.aia.common.model.repo.annotations.RepoSearchable;
import com.amdocs.aia.common.model.transformation.TransformationContext;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.amdocs.aia.il.common.model.configuration.transformation.Transformation.TRANSFORMATION_TYPE;

/**
 * Created by SWARNIMJ
 */
public class Context extends AbstractPublisherConfigurationModel implements DerivedElement<TransformationContext> {
    private static final long serialVersionUID = 8773789304118686580L;

    public static final String ELEMENT_TYPE = getElementTypeFor(Context.class);

    @RepoSearchable
    private String contextKey;

    private List<ContextEntity> contextEntities;

    public Context() {
        super.setElementType(ELEMENT_TYPE);
    }

    public String getContextKey() {
        return contextKey;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = StringUtils.removeEndIgnoreCase(contextKey, "Context");
    }

    public List<ContextEntity> getContextEntities() {
        return contextEntities;
    }

    public void setContextEntities(List<ContextEntity> contextEntities) {
        this.contextEntities = contextEntities;
    }

    @Override
    public TransformationContext toSharedElement() {
        TransformationContext context = super.cloneSharedElement(TransformationContext.class);
        context.setSourceElementId(this.getId());
        context.setContextKey(this.contextKey);
        context.setTransformationType(TRANSFORMATION_TYPE);
        context.setContextEntities(this.contextEntities.stream().map(ContextEntity::toSharedElement).collect(Collectors.toList()));
        return context;
    }
}