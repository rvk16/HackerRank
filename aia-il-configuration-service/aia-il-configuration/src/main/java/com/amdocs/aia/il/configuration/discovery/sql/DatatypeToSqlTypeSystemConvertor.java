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
                    result = String.format("%s(%d)", SqlTypeSystem.VARCHAR.getDatatypeKey(), columnSize);
                }
                else
                {
                    result = String.format("%s(%d)", SqlTypeSystem.VARCHAR.getDatatypeKey(), defaultVarcharLength);
                }
                break;
            case Types.DECIMAL:
                if(columnSize <= maxPrecision && columnSize + scale <= maxPrecision+ maxScale) {
                    result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL.getDatatypeKey(), columnSize, scale);
                }
                else{
                    result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL.getDatatypeKey(), maxPrecision, maxScale);
                }
                break;
            case Types.BIT:
                result = SqlTypeSystem.BOOLEAN.getDatatypeKey();
                break;
            case Types.NUMERIC: //NUMBER
                if(columnSize <= 0 || scale < 0) //for NUMBER without scale and precision (this means max value)
                {
                    result = SqlTypeSystem.DOUBLE.getDatatypeKey();
                }
                else if(scale > 0)
                {
                    if(columnSize <= maxPrecision && columnSize + scale <= maxPrecision + maxScale ) {
                        result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL.getDatatypeKey(), columnSize, scale);
                    }
                    else{
                        result = String.format("%s(%d,%d)", SqlTypeSystem.DECIMAL.getDatatypeKey(), maxPrecision, maxScale);
                    }
                }
                else{
                    if(columnSize <= 9){ //max digits for integer
                        result = SqlTypeSystem.INTEGER.getDatatypeKey();
                    }
                    else{
                        result = SqlTypeSystem.BIGINT.getDatatypeKey();
                    }
                }
                break;
            case Types.TINYINT:
            case Types.INTEGER:
            case Types.SMALLINT:
                result = SqlTypeSystem.INTEGER.getDatatypeKey();
                break;
            case Types.BIGINT:
                result = SqlTypeSystem.BIGINT.getDatatypeKey();
                break;
            case Types.REAL:
                result = SqlTypeSystem.DOUBLE.getDatatypeKey();
                break;
            case Types.FLOAT:
                if(columnSize <= 24){
                    result = SqlTypeSystem.FLOAT.getDatatypeKey();
                }
                else{
                    result = SqlTypeSystem.DOUBLE.getDatatypeKey();
                }
                break;
            case 100: //BINARY_FLOAT
                result = SqlTypeSystem.FLOAT.getDatatypeKey();
                break;
            case Types.DOUBLE:
            case 101: //BINARY_DOUBLE
                result = SqlTypeSystem.DOUBLE.getDatatypeKey();
                break;
            case Types.TIMESTAMP: //TODO TIME_WITH_TIMEZONE TIMESTAMP_WITH_TIMEZONE ??
            case Types.DATE:
            case Types.TIME:
                result = SqlTypeSystem.TIMESTAMP.getDatatypeKey();
                break;
            default:
                result = String.format("%s(%d)", SqlTypeSystem.VARCHAR.getDatatypeKey(), defaultVarcharLength);
                break;
        }
        return result;
    }
}
