package com.amdocs.aia.il.configuration.message;

import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;

public final class AiaApiMessages {
    private AiaApiMessages() {
        // singleton
    }

    public static final class GENERAL {
        public static final AIAAPIMessageTemplate UNEXPECTED_SERVER_ERROR =
                new AIAAPIMessageTemplate(12, "general.unexpected.server.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate REPOSITORY_ERROR =
                new AIAAPIMessageTemplate(13, "general.repository.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate ID_NOT_SET =
                new AIAAPIMessageTemplate(14, "general.id.not.set", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate OBJECT_ENTITY_EXIST =
                new AIAAPIMessageTemplate(15, "general.entity.already.exists", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate OBJECT_ENTITIES_EXIST =
                new AIAAPIMessageTemplate(15, "general.entities.already.exist", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate ENTITY_DOES_NOT_EXIST =
                new AIAAPIMessageTemplate(16, "general.entity.does.not.exist", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate ENTITIES_DO_NOT_EXIST =
                new AIAAPIMessageTemplate(16, "general.entities.do.not.exist", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate CANNOT_DELETE_ELEMENT =
                new AIAAPIMessageTemplate(23, "general.cannot.delete.element", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate CANNOT_SAVE_ELEMENT =
                new AIAAPIMessageTemplate(24, "general.cannot.save.element", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate INVALID_PK =
                new AIAAPIMessageTemplate(25, "general.invalid.pk", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate INVALID_INDEX_COLUMN =
                new AIAAPIMessageTemplate(26, "general.invalid.index.column", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate INVALID_INDEX_MISSING_NAME=
                new AIAAPIMessageTemplate(27, "general.invalid.index.missing.name", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate INVALID_INDEX_DUPLICATE_NAME=
                new AIAAPIMessageTemplate(28, "general.invalid.index.duplicate.name", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate AT_LEAST_ONE_COLUMN_INDEX_NAME=
                new AIAAPIMessageTemplate(29, "general.at.least.one.column", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate KEY_NOT_SET =
                new AIAAPIMessageTemplate(30, "general.subject.area.key.not.set", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate OBJECT_DOES_NOT_EXIST = new AIAAPIMessageTemplate(30, "general.object.does.not.exist", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate UNEXPECTED_OPERATION_ERROR = new AIAAPIMessageTemplate(31, "general.unexpected.operation.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate VALIDATION_ERROR = new AIAAPIMessageTemplate(99, "general.validation.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate AT_LEAST_ONE_CONTEXT_ENTITY_EXIST= new AIAAPIMessageTemplate(32, "general.at.least.one.context.entity", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate VALIDATION_ERROR_MISSING_REQUIRED_ATTRIBUTES = new AIAAPIMessageTemplate(33, "general.missing.transformation.attribute", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate VALIDATION_ERROR_MISSING_RELATION_ATTRIBUTES = new AIAAPIMessageTemplate(34, "general.missing.transformation.relation", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DISCOVERY_MISSING_REQUIRED_PARAMETERS = new AIAAPIMessageTemplate(35, "discovery.missing.required.parameters", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DISCOVERY_INVALID_PARAMETER_VALUE = new AIAAPIMessageTemplate(36, "discovery.invalid.parameter.value", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DISCOVERY_MISSING_CSV_HEADER = new AIAAPIMessageTemplate(37, "discovery.csv.file.missing.header", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DISCOVERY_FAILED_DOWNLOADING_FILE = new AIAAPIMessageTemplate(38, "discovery.failed.downloading.file", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DISCOVERY_FILE_NOT_FOUND = new AIAAPIMessageTemplate(39, "discovery.file.not.found", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DISCOVERY_FAILED_IO_ERROR = new AIAAPIMessageTemplate(40, "discovery.failed.io.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate ERROR_WHILE_UPDATING_CR_WRITE_MODE = new AIAAPIMessageTemplate(41, "general.error.updating.change.request.write.mode", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate OBJECT_KEY_NOT_COMPATIBLE= new AIAAPIMessageTemplate(42, "general.object.key.not.compatible", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DUPLICATE_ATTRIBUTE = new AIAAPIMessageTemplate(43, "general.duplicate.attribute.key", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate SAME_FILTER_KEY_AND_ATTRIBUTE_KEY = new AIAAPIMessageTemplate(44, "general.same.filter.key.and.attribute.key", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate DISCOVERY_BAD_SWAGGER_FILE = new AIAAPIMessageTemplate(45, "discovery.json.bad.swagger.file", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate INVALID_INDEX_SIZE = new AIAAPIMessageTemplate(46, "general.invalid.index.size", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate SERIALIZATION_ID_ALREADY_EXISTS = new AIAAPIMessageTemplate(47, "general.serialization.Id.already.exists", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate NOT_ALLOWED_TO_MODIFY_SERIALIZATION_ID= new AIAAPIMessageTemplate(48, "general.not.allowed.to.modify.serialization.Id", AIAAPIMessageTemplate.MessageType.ERROR);

        public static final AIAAPIMessageTemplate MISSING_SCHEMA =
                new AIAAPIMessageTemplate(47, "general.missing.schema", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate INVALID_LEAD_CONTEXT_ENTITY =
                new AIAAPIMessageTemplate(50, "general.invalid.lead.context.entity", AIAAPIMessageTemplate.MessageType.ERROR);

        public static final AIAAPIMessageTemplate EXPORT_TO_ZIP_ERROR =
                new AIAAPIMessageTemplate(51, "general.export.to.zip.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate EXPORT_JACKSON_ERROR =
                new AIAAPIMessageTemplate(52, "general.export.jackson.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate IMPORT_FROM_ZIP_ERROR =
                new AIAAPIMessageTemplate(9, "general.import.from.zip", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate IMPORT_FROM_ZIP_MISSING_CSV_ERROR =
                new AIAAPIMessageTemplate(10, "general.import.from.zip.missing.csv", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate IMPORT_FROM_ZIP_NOT_ZIP_ERROR =
                new AIAAPIMessageTemplate(11, "general.import.from.zip.not.zip", AIAAPIMessageTemplate.MessageType.ERROR);

        public static final AIAAPIMessageTemplate INVALID_EXTERNAL_COLLECTION_RULES =
                new AIAAPIMessageTemplate(53, "general.invalid.external.collection.rules", AIAAPIMessageTemplate.MessageType.ERROR);


        private GENERAL() {
            // singleton
        }
    }
}