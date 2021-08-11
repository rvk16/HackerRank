package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.common.model.extensions.typesystems.SqlTypeSystem;

import java.sql.Types;

public class DatatypeToSqlTypeSystemConvertor {

    public static String getDatatypeInSqlTypeSystem(ColumnInfo columnInfo,int maxPrecision, int maxScale, int defaultVarcharLength) {
        String result;
        int columnSize = columnInfo.getColumnSize();
        int scale = columnInfo.getDecimalDigits();
        switch (columnInfo.getDatatype()) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                if(columnSize > 0)
                {
                    result = String.format("%s(%d)", SqlTypeSystem.VARCHAR, columnSize);
                }
                else
                {
                    result = String.format("%s(%d)", SqlTypeSystem.VARCHAR, defaultVarcharLength);
                }
                break;
            case Types.DECIMAL:
                if(columnSize <= maxPrecision && columnSize + scale <= maxPrecision+ maxScale) {
                    result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL, columnSize, scale);
                }
                else{
                    result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL, maxPrecision, maxScale);
                }
                break;
            case Types.BIT:
                result = SqlTypeSystem.BOOLEAN;
                break;
            case Types.NUMERIC: //NUMBER
                if(columnSize <= 0 || scale < 0) //for NUMBER without scale and precision (this means max value)
                {
                    result = SqlTypeSystem.DOUBLE;
                }
                else if(scale > 0)
                {
                    if(columnSize <= maxPrecision && columnSize + scale <= maxPrecision + maxScale ) {
                        result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL, columnSize, scale);
                    }
                    else{
                        result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL, maxPrecision, maxScale);
                    }
                }
                else{
                    if(columnSize <= 9){ //max digits for integer
                        result = SqlTypeSystem.INTEGER;
                    }
                    else{
                        result = SqlTypeSystem.BIGINT;
                    }
                }
                break;
            case Types.TINYINT:
            case Types.INTEGER:
            case Types.SMALLINT:
                result = SqlTypeSystem.INTEGER;
                break;
            case Types.BIGINT:
                result = SqlTypeSystem.BIGINT;
                break;
            case Types.REAL:
                result = SqlTypeSystem.DOUBLE;
                break;
            case Types.FLOAT:
                if(columnSize <= 24){
                    result = SqlTypeSystem.FLOAT;
                }
                else{
                    result = SqlTypeSystem.DOUBLE;
                }
                break;
            case 100: //BINARY_FLOAT
                result = SqlTypeSystem.FLOAT;
                break;
            case Types.DOUBLE:
            case 101: //BINARY_DOUBLE
                result = SqlTypeSystem.DOUBLE;
                break;
            case Types.TIMESTAMP: //TODO TIME_WITH_TIMEZONE TIMESTAMP_WITH_TIMEZONE ??
            case Types.DATE:
            case Types.TIME:
                result = SqlTypeSystem.TIMESTAMP;
                break;
            default:
                result = String.format("%s(%d)", SqlTypeSystem.VARCHAR, defaultVarcharLength);
                break;
        }
        return result;
    }
}
