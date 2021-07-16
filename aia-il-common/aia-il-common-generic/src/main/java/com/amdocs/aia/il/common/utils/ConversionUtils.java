package com.amdocs.aia.il.common.utils;

import com.amdocs.aia.common.model.extensions.typesystems.SqlTypeSystem;
import com.amdocs.aia.common.model.extensions.typesystems.TypeSystemFactory;
import com.amdocs.aia.common.model.logical.PrimitiveDatatype;
import com.amdocs.aia.common.model.utils.TypeSystemUtils;
import com.amdocs.aia.il.common.constant.JavaTypes;
import com.amdocs.aia.il.common.constant.ProtoTypes;
import com.amdocs.aia.il.common.constant.SqlTypes;
import com.amdocs.aia.il.common.log.LogMsg;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionUtils.class);
    private static final int MAX_PRECISION = 38;
    private static final String ERROR_PROTO_TYPE_NOT_SUPPORTED = "ERROR_PROTO_TYPE_NOT_SUPPORTED";
    private static final String ERROR_FIELD_DATA_TYPE_NOT_SUPPORTED = "ERROR_FIELD_DATA_TYPE_NOT_SUPPORTED";

    private static final String PROTO = "proto";
    private static final String ORACLE = "oracle";

    private ConversionUtils() {
        // Utils class
    }

    public static String resolveSqlType(String type, String dataType) { // NOSONAR
        if (PROTO.equalsIgnoreCase(type)) {
            return resolveProtoToSQLType(dataType);
        } else if (ORACLE.equalsIgnoreCase(type)) {
            return resolveOracleToSQLType(dataType);
        } else {
            return null;
        }
    }

    public static String getRawSqlType(String sqlType) { // NOSONAR
        String rawType = TypeSystemUtils.extractRawType(sqlType);
        if(rawType.equals(SqlTypeSystem.DECIMAL)){
            rawType = SqlTypeSystem.DOUBLE;
        }
        return rawType;
    }

    public static String resolveProtoToSQLType(String prototype) { // NOSONAR
        switch (prototype) {
            case ProtoTypes.BOOL:
                return SqlTypes.BOOLEAN;
            case ProtoTypes.INT32:
                return SqlTypes.INTEGER;
            case ProtoTypes.TIMESTAMP:
            case ProtoTypes.INT64:
                return SqlTypes.BIGINT;
            case ProtoTypes.FLOAT:
                return SqlTypes.FLOAT;
            case ProtoTypes.BIG_DECIMAL:
            case ProtoTypes.DOUBLE:
                return SqlTypes.DOUBLE;
            case ProtoTypes.STRING:
                return SqlTypes.VARCHAR;
            default: {
                return protoTypeCondChk(prototype);
            }
        }
    }

    private static String protoTypeCondChk(String prototype) {
        if (prototype.startsWith(ProtoTypes.BIG_DECIMAL)) {
            return SqlTypes.DOUBLE;
        } else {
            String message = ERROR_PROTO_TYPE_NOT_SUPPORTED + prototype;
            LOGGER.error(message);
            throw new RuntimeException(message);
        }
    }

    public static String resolveOracleToSQLType(String oracleType) { // NOSONAR
        String res;
        if (oracleType.startsWith("NUMBER")) {
            res = SqlTypes.DOUBLE; // default to double
            String paramsText = extractTextFromParentheses(oracleType);
            if (StringUtils.isNotBlank(paramsText)) {
                String[] params = paramsText.split(",");
                int precision = params[0].equals("*") ? MAX_PRECISION : Integer.parseInt(params[0]);
                int scale = params.length > 1 ? Integer.parseInt(params[1]) : 0;
                res = getTypeForPrecisionAndScale(precision, scale);
            }
        } else if (oracleType.startsWith("FLOAT")) {
            res = SqlTypes.DOUBLE; // default to double
            String precisionText = extractTextFromParentheses(oracleType);
            if (StringUtils.isNotBlank(precisionText) && (Integer.parseInt(precisionText) <= 24)) {
                    res = SqlTypes.FLOAT;
            }
        } else if (oracleType.startsWith("TIMESTAMP")) {
            // may contain precision information (we don't care what the precision really is - we will always map to proto TIMESTAMP)
            res = SqlTypes.TIMESTAMP;
        } else {
            switch (oracleType) {
                case "BINARY_FLOAT":
                    res = SqlTypes.FLOAT;
                    break;
                case "BINARY_DOUBLE":
                    res = SqlTypes.DOUBLE;
                    break;
                case "TIMESTAMP":
                case "DATE":
                case "TIME":
                    res = SqlTypes.TIMESTAMP;
                    break;
                default:
                    res = SqlTypes.VARCHAR;
            }
        }
        return res;
    }

    private static String extractTextFromParentheses(String s) {
        String res = null;
        String exp = "\\(([^)]+)\\)";
        Matcher m = Pattern.compile(exp).matcher(s);
        if (m.find()) {
            res = m.group(1);
        }
        return res;
    }

    private static String getTypeForPrecisionAndScale(int precision, int scale) {
        String res = SqlTypes.DOUBLE; // default is DOUBLE
        if (scale == 0 && precision > 0) {
            // an integer/long value (no scale)
            res = precision <= 4 ? SqlTypes.INTEGER : SqlTypes.BIGINT;
        }
        return res;
    }

    public static String resolveJavaType(String sqlType) { // NOSONAR
        switch (sqlType) {
            case SqlTypes.BIGINT:
                return JavaTypes.LONG;
            case SqlTypes.BOOLEAN:
                return JavaTypes.BOOLEAN;
            case SqlTypes.INTEGER:
                return JavaTypes.INT;
            case SqlTypes.FLOAT:
            case SqlTypes.BINARY_FLOAT:
                return JavaTypes.FLOAT;
            case SqlTypes.BINARY_DOUBLE:
            case SqlTypes.DOUBLE:
                return JavaTypes.DOUBLE;
            case SqlTypes.VARCHAR:
            default:
                return JavaTypes.STRING;
        }
    }

    public static String getSQLType(String primitiveType) {
        switch (primitiveType) {
            case JavaTypes.BOOLEAN:
                return SqlTypes.BOOLEAN;
            case JavaTypes.INT:
                return SqlTypes.INTEGER;
            case JavaTypes.TIMESTAMP:
            case JavaTypes.LONG:
                return SqlTypes.BIGINT;
            case JavaTypes.FLOAT:
                return SqlTypes.FLOAT;
            case JavaTypes.DOUBLE:
                return SqlTypes.DOUBLE;
            case JavaTypes.STRING:
                return SqlTypes.VARCHAR;
            default:
                //not supported
                LOGGER.error(LogMsg.getMessage(ERROR_FIELD_DATA_TYPE_NOT_SUPPORTED, primitiveType));
                throw new RuntimeException(LogMsg.getMessage(ERROR_FIELD_DATA_TYPE_NOT_SUPPORTED, primitiveType));
        }
    }

    public static String getSQLTypeForTargetEntity(PrimitiveDatatype dataType) {
        switch (dataType) {
            case BOOLEAN:
                return SqlTypes.BOOLEAN;
            case TIMESTAMP:
            case LONG:
                return SqlTypes.BIGINT;
            case INTEGER:
                return SqlTypes.INTEGER;
            case FLOAT:
                return SqlTypes.FLOAT;
            case DOUBLE:
            case DECIMAL:
                return SqlTypes.DOUBLE;
            case STRING:
                return SqlTypes.VARCHAR;
            case BINARY:
                return SqlTypes.BINARY_DOUBLE;
            default:
                //not supported
                LOGGER.error(LogMsg.getMessage(ERROR_FIELD_DATA_TYPE_NOT_SUPPORTED, dataType.name()));
                throw new RuntimeException(LogMsg.getMessage(ERROR_FIELD_DATA_TYPE_NOT_SUPPORTED, dataType.name()));
        }
    }

    public static Class<? extends  Exception> getExceptionType(Exception exception){
        Throwable rootCause = exception.getCause();
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return (Class<? extends Exception>) rootCause.getClass();

    }

}
