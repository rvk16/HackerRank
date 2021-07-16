package com.amdocs.aia.il.common.model.external;

import com.amdocs.aia.il.common.model.physical.PhysicalStoreType;

import java.util.Arrays;
import java.util.List;

public class ExternalSchemaStoreTypes {
    // we have these constants (and not an enum) because of 2 reasons:
    // 1. Jackson's support for polymorphic types requires using string constants as type discriminator (it cannot use enums)
    // 2. For forward compatibility, to allow supporting custom types as well
    public static final String CSV = "csv";
    public static final String KAFKA = "kafka";
    public static final String SQL = "sql";

    public static final List<String> ALL_TYPES = Arrays.asList(CSV, KAFKA, SQL);

    public static PhysicalStoreType toPhysicalStoreType(String externalSchemaStoreType) {
        switch (externalSchemaStoreType) {
            case CSV:
                return PhysicalStoreType.CSV;
            case KAFKA:
                return PhysicalStoreType.KAFKA;
            case SQL:
                return PhysicalStoreType.SQL;
            default:
                throw new IllegalStateException("Unexpected value: " + externalSchemaStoreType);
        }
    }

    public static String fromPhysicalStoreType(PhysicalStoreType physicalStoreType) {
        switch (physicalStoreType) {
            case CSV:
                return CSV;
            case KAFKA:
                return KAFKA;
            case SQL:
                return SQL;
            default:
                throw new IllegalStateException("Unexpected value: " + physicalStoreType);
        }
    }
}
