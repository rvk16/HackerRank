package com.amdocs.aia.il.configuration.mapper.external;

import com.amdocs.aia.common.core.web.AiaInternalServerException;
import com.amdocs.aia.il.common.model.external.CollectorChannelType;
import com.amdocs.aia.il.common.model.external.ExternalSchemaCollectionRules;
import com.amdocs.aia.il.configuration.dto.ExternalSchemaCollectionRulesDTO;
import com.amdocs.aia.il.configuration.message.MessageHelper;

import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractExternalSchemaCollectionRulesInfoMapper<M extends ExternalSchemaCollectionRules, D extends ExternalSchemaCollectionRulesDTO> implements ExternalSchemaCollectionRulesMapper<M, D> {

    private final String COLLECTOR_CHANNEL_TYPE = "CollectorChannelType";

    private MessageHelper messageHelper;

    private Class<M> modelClass;
    private Class<D> dtoClass;

    protected AbstractExternalSchemaCollectionRulesInfoMapper() {
    }

    @Inject
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    protected MessageHelper getMessageHelper() {
        return messageHelper;
    }

    protected void lazyInit() {
        final Type[] actualTypeArguments = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        modelClass = (Class<M>) actualTypeArguments[0];
        dtoClass = (Class<D>) actualTypeArguments[1];
    }

    private Class<M> getModelClass() {
        if (modelClass == null) {
            lazyInit();
        }
        return modelClass;
    }

    public Class<D> getDtoClass() {
        if (dtoClass == null) {
            lazyInit();
        }
        return dtoClass;
    }

    @Override
    public M toModel(D dto) {
        try {
            final M model = getModelClass().getConstructor().newInstance();
            model.setOngoingChannel(parseChannelTypeFromDTO(dto.getOngoingChannel()));
            model.setInitialLoadChannel(parseChannelTypeFromDTO(dto.getInitialLoadChannel()));
            model.setReplayChannel(parseChannelTypeFromDTO(dto.getReplayChannel()));
            model.setPartialLoadRelativeURL(dto.getPartialLoadRelativeURL());
            model.setInitialLoadRelativeURL(dto.getInitialLoadRelativeURL());
            return model;
        } catch (Exception e) {
            throw new AiaInternalServerException(e);
        }
    }


    @Override
    public D toDTO(M model) {
        try {
            if (model == null) return null;
            D dto = getDtoClass().getConstructor().newInstance();
            dto.setOngoingChannel(model.getOngoingChannel().name());
            dto.setInitialLoadChannel(model.getInitialLoadChannel().name());
            dto.setReplayChannel(model.getReplayChannel().name());
            dto.setInitialLoadRelativeURL(model.getInitialLoadRelativeURL());
            dto.setPartialLoadRelativeURL(model.getPartialLoadRelativeURL());
            return dto;
        } catch (Exception e) {
            throw new AiaInternalServerException(e);
        }
    }

    private CollectorChannelType parseChannelTypeFromDTO(String channelTypeString) {
        try {
            return CollectorChannelType.valueOf(channelTypeString);
        } catch (IllegalArgumentException e) {
            throw messageHelper.createElementNotFoundException(COLLECTOR_CHANNEL_TYPE, channelTypeString);
        }

    }

}
