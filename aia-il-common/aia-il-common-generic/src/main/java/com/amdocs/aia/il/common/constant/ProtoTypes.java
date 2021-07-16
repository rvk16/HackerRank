package com.amdocs.aia.il.common.constant;

public class ProtoTypes {

    // built-in types
    public static final String STRING = "string";
    public static final String INT32 = "int32";
    public static final String INT64 = "int64";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BOOL = "bool";

    // adh custom types
    public static final String ADH_DATATYPES_PACKAGE_PREFIX = "com.amdocs.adh.core.proto.datatypes.";
    public static final String TIMESTAMP_SHORT_NAME = "Timestamp";
    public static final String MAP_SHORT_NAME = "Map";
    public static final String BIG_DECIMAL_SHORT_NAME = "BigDecimal";
    public static final String TIMESTAMP = ADH_DATATYPES_PACKAGE_PREFIX + TIMESTAMP_SHORT_NAME;
    public static final String MAP = ADH_DATATYPES_PACKAGE_PREFIX + MAP_SHORT_NAME;
    public static final String BIG_DECIMAL = ADH_DATATYPES_PACKAGE_PREFIX + BIG_DECIMAL_SHORT_NAME;

}
