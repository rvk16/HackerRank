package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.il.configuration.discovery.YamlFileApplicationContextInitializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes=ExternalSqlDiscoveryConfigurationProperties.class ,initializers = YamlFileApplicationContextInitializer.class)
class DatatypeToSqlTypeSystemConvertorTest {

    @Autowired
    ExternalSqlDiscoveryConfigurationProperties properties;

    @Test
    public void testDataTypesConversion() {

        String numberNoConstraints = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("NUMBER_NO_CONSTRAINTS", Types.NUMERIC, 0, -127), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DOUBLE", numberNoConstraints);
        String binaryFloat = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("BINARY_FLOAT", 100, 4, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("FLOAT", binaryFloat);
        String binaryDouble = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("BINARY_DOUBLE", 101, 8, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DOUBLE", binaryDouble);
        String floatNoConstraints = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("FLOAT_NO_CONSTRAINTS", Types.FLOAT, 126, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DOUBLE", floatNoConstraints);
        String float5 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("FLOAT_5", Types.FLOAT, 5, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("FLOAT", float5);
        String float25 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("FLOAT_25", Types.FLOAT, 25, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DOUBLE", float25);
        String number1_minus1 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("NUMBER1_1", Types.NUMERIC, 1, -1), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DOUBLE", number1_minus1);
        String number0_0 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("NUMBER0_0", Types.NUMERIC, 0, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DOUBLE", number0_0);
        String number1_0 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("NUMBER1_0", Types.NUMERIC, 1, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("INTEGER", number1_0);
        String number3_0 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("NUMBER3_0", Types.NUMERIC, 3, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("INTEGER", number3_0);
        String number12_0 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("NUMBER12_0", Types.NUMERIC, 12, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("BIGINT", number12_0);
        String number12_2 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("NUMBER12_2", Types.NUMERIC, 12, 2), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DECIMAL(12,2)", number12_2);
        String date = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("DATE", Types.DATE, 7, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("TIMESTAMP", date);
        String time = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("TIME", Types.TIME, 12, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("TIMESTAMP", time);
        String timestamp = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("TIMESTAMP", Types.TIMESTAMP, 12, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("TIMESTAMP", timestamp);
        String char0 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("CHAR0", Types.CHAR, 0, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", char0);
        String varchar0 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("VARCHAR0", Types.VARCHAR, 0, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", varchar0);
        String otherType = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("otherType", 8149, 0, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", otherType);
        String blob = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("blob", Types.BLOB, 200, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", blob);
        String clob = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("blob", Types.CLOB, 489, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", clob);
        String mslable = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("mslable", 1111, 1, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", mslable);
        String real = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("real", 6, 63, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("DOUBLE", real);
        String httpuri = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("httpuri", 1111, 4, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", httpuri);
        String xml = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("xml", 2009, 2000, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(4000)", xml);
        String smallint = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("smallint", 2, 38, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("BIGINT", smallint); //TODO smallint in oracle is represented with number(38)
        String varchar6 = DatatypeToSqlTypeSystemConvertor.getDatatypeInSqlTypeSystem(new ColumnInfo("varchar6", Types.VARCHAR, 6, 0), properties.getMaxPrecision(), properties.getMaxScale(), properties.getDefaultVarcharLength());
        assertEquals("VARCHAR(6)", varchar6);
    }
}