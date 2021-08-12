package com.amdocs.aia.il.configuration.discovery.sql;

import com.amdocs.aia.il.configuration.discovery.DiscoveryRuntimeException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DatabaseIntrospector {
    private final String COLUMN_NAME = "COLUMN_NAME";

    public List<TableInfo> getTablesMetadata(Connection connection) {
        List<TableInfo> tableInfoList = new ArrayList<>();
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            // see https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getTables-java.lang.String-java.lang.String-java.lang.String-java.lang.String:A-
            // see https://dzone.com/articles/jdbc-tutorial-extracting-database-metadata-via-jdb
            try (ResultSet rs = getTableResultSet(databaseMetaData)) {
                while (rs.next()) {
                    tableInfoList.add(getTableInfo(databaseMetaData, rs));
                }
            }
        } catch (SQLException e) {
            throw new DiscoveryRuntimeException(e);
        }
        return tableInfoList;
    }

    /**
     * according to the database policies, determines using which name we need to access the metadata of database
     * objects (lowercase/uppercase/mixed case)
     */
    private String toIdentifierName(DatabaseMetaData databaseMetaData, String objectName) {
        try {
            if (databaseMetaData.storesLowerCaseIdentifiers()) {
                return objectName.toLowerCase();
            } else if (databaseMetaData.storesUpperCaseIdentifiers()) {
                return objectName.toUpperCase();
            } else {
                return objectName;
            }
        } catch (SQLException e) {
            //throw new todo(e);
        }
        return null;
    }

    private ResultSet getTableResultSet(DatabaseMetaData databaseMetaData) throws SQLException {
        return databaseMetaData.getTables(databaseMetaData.getConnection().getCatalog(), databaseMetaData.getConnection().getSchema(), null, new String[]{"TABLE", "VIEW", "PARTITIONED TABLE"});
    }

    private TableInfo getTableInfo(DatabaseMetaData databaseMetaData, ResultSet tableResultSet) throws SQLException {
        String tableName = tableResultSet.getString("TABLE_NAME");
        PrimaryKeyInfo primaryKey = getPrimaryKeyInfo(databaseMetaData, tableName);
        List<ColumnInfo> columns = getColumnInfos(databaseMetaData, tableName);
        List<IndexInfo> indexes = getIndexInfos(databaseMetaData, tableName);
        return new TableInfo(tableName, columns, primaryKey, indexes);
    }

    private List<ColumnInfo> getColumnInfos(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        ResultSet columnsResultSet = databaseMetaData.getColumns(databaseMetaData.getConnection().getCatalog(), databaseMetaData.getConnection().getSchema(), toIdentifierName(databaseMetaData, tableName), null);
        List<ColumnInfo> columns = new ArrayList<>();
        while (columnsResultSet.next()) {
            // see https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getColumns-java.lang.String-java.lang.String-java.lang.String-java.lang.String-
            String columnName = columnsResultSet.getString(COLUMN_NAME);
            int datatype = columnsResultSet.getInt("DATA_TYPE");
            int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
            int decimalDigits = columnsResultSet.getInt("DECIMAL_DIGITS");
            columns.add(new ColumnInfo(columnName, datatype, columnSize,decimalDigits));
        }
        return columns;
    }

    private List<IndexInfo> getIndexInfos(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        ResultSet indexesResultSet = databaseMetaData.getIndexInfo(databaseMetaData.getConnection().getCatalog(), databaseMetaData.getConnection().getSchema(), toIdentifierName(databaseMetaData, tableName), false, false);
        List<IndexInfo> indexes = new ArrayList<>();
        // NOTE: for composite indexes, the JDBC API returns a single row (in the result set) for each column in the index. So we'll have to do the 'grouping' ourselves
        List<IndexColumn> indexColumns = new ArrayList<>();
        while (indexesResultSet.next()) {
            // see https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getIndexInfo-java.lang.String-java.lang.String-java.lang.String-boolean-boolean-
            String indexName = indexesResultSet.getString("INDEX_NAME");
            boolean nonUnique = indexesResultSet.getBoolean("NON_UNIQUE");
            String columnName = indexesResultSet.getString(COLUMN_NAME);
            int ordinalPosition = indexesResultSet.getInt("ORDINAL_POSITION");
            if (StringUtils.isEmpty(indexName) || StringUtils.isEmpty(columnName) || ordinalPosition == 0) {
                // irrelevant row
                continue;
            }
            indexColumns.add(new IndexColumn(indexName, columnName, ordinalPosition));
            if (ordinalPosition == 1) {
                // we create and add an index instance only once per index (no need to do it for each column)
                // for now we add the index WITHOUT the column names. we will set the columns in the end, after we have all index rows
                indexes.add(new IndexInfo(indexName, Collections.emptyList(), !nonUnique));
            }
        }
        Map<String, List<IndexColumn>> columnsPerIndex = indexColumns.stream().collect(Collectors.groupingBy(IndexColumn::getIndexName));
        indexes.forEach(index -> index.setColumnNames(columnsPerIndex.get(index.getIndexName()).stream()
                .sorted(Comparator.comparing(IndexColumn::getOrdinalPosition))
                .map(IndexColumn::getColumnName)
                .collect(Collectors.toList())));
        return indexes;
    }

    @Nullable
    private PrimaryKeyInfo getPrimaryKeyInfo(DatabaseMetaData databaseMetaData, String tableName) throws SQLException {
        ResultSet primaryKeyResultSet = databaseMetaData.getPrimaryKeys(databaseMetaData.getConnection().getCatalog(), databaseMetaData.getConnection().getSchema(), toIdentifierName(databaseMetaData, tableName));
        List<KeyColumn> keyColumns = new ArrayList<>();
        while (primaryKeyResultSet.next()) {
            String columnName = primaryKeyResultSet.getString(COLUMN_NAME);
            int ordinalPosition = primaryKeyResultSet.getInt("KEY_SEQ");
            keyColumns.add(new KeyColumn(columnName, ordinalPosition));
        }
        List<String> columnNames = keyColumns.stream()
                .sorted(Comparator.comparing(KeyColumn::getOrdinalPosition))
                .map(KeyColumn::getColumnName)
                .collect(Collectors.toList());
        return columnNames.isEmpty() ? null : new PrimaryKeyInfo(columnNames);
    }

    // an internal class for storing a column name, along with its position, in  primary keys
    private static class KeyColumn {
        String columnName;
        int ordinalPosition;

        public KeyColumn(String columnName, int ordinalPosition) {
            this.columnName = columnName;
            this.ordinalPosition = ordinalPosition;
        }

        public String getColumnName() {
            return columnName;
        }

        public int getOrdinalPosition() {
            return ordinalPosition;
        }
    }

    // an internal class for storing a column name, along with its position, in composite indexes
    private static class IndexColumn {
        String indexName;
        String columnName;
        int ordinalPosition;

        public IndexColumn(String indexName, String columnName, int ordinalPosition) {
            this.indexName = indexName;
            this.columnName = columnName;
            this.ordinalPosition = ordinalPosition;
        }

        public String getIndexName() {
            return indexName;
        }

        public String getColumnName() {
            return columnName;
        }

        public int getOrdinalPosition() {
            return ordinalPosition;
        }
    }
}