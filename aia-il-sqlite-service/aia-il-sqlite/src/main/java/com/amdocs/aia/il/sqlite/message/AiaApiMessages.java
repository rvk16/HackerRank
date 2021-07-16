package com.amdocs.aia.il.sqlite.message;

import com.amdocs.aia.common.core.web.AIAAPIMessageTemplate;

public final class AiaApiMessages {
    private AiaApiMessages() {
        // singleton
    }

    public static final class GENERAL {
        public static final AIAAPIMessageTemplate UNEXPECTED_SERVER_ERROR =
                new AIAAPIMessageTemplate(12, "general.unexpected.server.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate VALIDATION_ERROR =
                new AIAAPIMessageTemplate(19, "general.validation.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate SQL_ERROR =
                new AIAAPIMessageTemplate(21, "general.sql.error", AIAAPIMessageTemplate.MessageType.ERROR);
        public static final AIAAPIMessageTemplate FAILED_TO_CREATE_EXPORT_FILE =
                new AIAAPIMessageTemplate(23, "general.export.file.error", AIAAPIMessageTemplate.MessageType.ERROR);

        private GENERAL() {
            // singleton
        }
    }
}