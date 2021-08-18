package com.amdocs.aia.il.configuration.message;

import com.amdocs.aia.common.core.utils.ValidityStatus;
import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;
import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.common.model.configuration.ConfigurationUtils;
import com.amdocs.aia.il.common.model.configuration.tables.AbstractPublisherConfigurationModel;
import com.amdocs.aia.il.common.model.external.CollectorChannelType;
import com.amdocs.aia.il.common.model.external.ExternalSchemaType;
import com.amdocs.aia.il.configuration.exception.ApiException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class MessageHelper {

    private final MessageSource messageSource;

    @Inject
    public MessageHelper(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String format(String key, Object... params) {
        return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
    }

    public String getElementDisplayType(Class<? extends AbstractPublisherConfigurationModel> clazz) {
        String elementType = ConfigurationUtils.getElementType(clazz);
        return getElementDisplayType(elementType);
    }

    public String getExternalSchemaTypeDisplayName(ExternalSchemaType externalSchemaType) {
        return format("external.schema.type." + externalSchemaType.name());
    }

    public String getCollectorChannelTypeDisplayName(CollectorChannelType channelType) {
        return format("collector.channel.type." + channelType.name());
    }

    public String getElementDisplayType(String elementType) {
        return format("element.type." + elementType);
    }

    public ApiException createElementNotFoundException(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.ENTITY_DOES_NOT_EXIST,
                getElementDisplayType(elementType),
                elementKey);
    }

    public ApiException createElementsNotFoundException(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.ENTITIES_DO_NOT_EXIST,
                getElementDisplayType(elementType),
                elementKey);
    }

    public ApiException createElementAlreadyExistsException(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST,
                getElementDisplayType(elementType),
                elementKey);
    }

    public ApiException createObjectAlreadyExistException(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITY_EXIST,
                getElementDisplayType(elementType),
                elementKey);
    }


    public ApiException createElementsAlreadyExistsException(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.OBJECT_ENTITIES_EXIST,
                getElementDisplayType(elementType),
                elementKey);
    }
    public ApiException invalidExternalCollectionRulesException(String elementType, String SchemaKey,String schemaType,String channel) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_EXTERNAL_COLLECTION_RULES,
                getElementDisplayType(elementType),
                SchemaKey,schemaType,channel);
    }




    public ApiException invalidPrimaryKeyException(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_PK,
                getElementDisplayType(elementType),
                elementKey);
    }

    public ApiException invalidExternalEntity(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.CANNOT_SAVE_ELEMENT,
                getElementDisplayType(elementType),
                elementKey);
    }

    public ApiException missingSchema(String elementType, String schemaKey ) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.MISSING_SCHEMA,
                getElementDisplayType(elementType),
                schemaKey);
    }

    public ApiException invalidIndexColumnException(String elementType, String elementKey, String tableName) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_INDEX_COLUMN,
                getElementDisplayType(elementType),
                elementKey, tableName);
    }

    public ApiException invalidIndexException(String elementType, String elementKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_INDEX_SIZE,
                getElementDisplayType(elementType),
                elementKey);
    }

    public ApiException invalidIndexMissingNameException(String elementType, String tableName) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_INDEX_MISSING_NAME,
                getElementDisplayType(elementType),
                tableName);
    }

    public ApiException invalidIndexDuplicateNameException(String elementType, String tableName) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_INDEX_DUPLICATE_NAME,
                getElementDisplayType(elementType),
                tableName);
    }

    public ApiException atLeastOneColumnException(String elementType, String tableName) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.AT_LEAST_ONE_COLUMN_INDEX_NAME,
                getElementDisplayType(elementType),
                tableName);
    }

    public ApiException createSubjectAreaKeyNotSetException(String elementType, String key, String msg) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.KEY_NOT_SET,
                getElementDisplayType(elementType),
                key, msg);
    }

    public ApiException createIDNotSetException(String elementType, String id, String msg) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.ID_NOT_SET,
                getElementDisplayType(elementType),
                id, msg);
    }


    public ApiException createObjectDoesNotExistException(String get_publisherTransformation, String elementType, String id) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.OBJECT_DOES_NOT_EXIST,
                get_publisherTransformation,
                getElementDisplayType(elementType),
                id);
    }

    public ApiException createUnexpectedOperationErrorException(String elementType, Exception e) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR, AiaApiMessages.GENERAL.UNEXPECTED_OPERATION_ERROR, e,
                getElementDisplayType(elementType));
    }

    public ApiException createValidationException(ValidityStatus validityStatus, String objectType) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.VALIDATION_ERROR, validityStatus,
                objectType);
    }

    public ApiException createAttributesValidationException(ValidityStatus validityStatus, AIAAPIMessageTemplate messageKey, String objectType) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, messageKey, validityStatus,
                objectType);
    }

    public ApiException serializationIdAlreadyExistsException(String entityOrAttributeKey, String providedKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.SERIALIZATION_ID_ALREADY_EXISTS,
                providedKey, entityOrAttributeKey);
    }

    public ApiException notAllowedToModifySerializationIdException(String entityOrAttributeKey, String providedKey) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.NOT_ALLOWED_TO_MODIFY_SERIALIZATION_ID,
                providedKey, entityOrAttributeKey);
    }

    public ApiException createPublisherContextDoesNotExistException(String get_publisherContext, String elementType, String id) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.NOT_FOUND, AiaApiMessages.GENERAL.AT_LEAST_ONE_CONTEXT_ENTITY_EXIST,
                get_publisherContext,
                getElementDisplayType(elementType),
                id);
    }


    public ApiException invalidLeadContextEntityException(String contextEntity,  String id) {
        return new ApiException(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST, AiaApiMessages.GENERAL.INVALID_LEAD_CONTEXT_ENTITY,
                contextEntity, id);
    }

}
