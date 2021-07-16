package com.amdocs.aia.il.sqlite.service;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.common.core.web.AiaApiMessage;
import com.amdocs.aia.il.sqlite.dto.ResultSetDTO;
import com.amdocs.aia.il.sqlite.message.AiaApiMessages;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.*;
import java.util.Date;

@Service
public class SqliteRestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqliteRestService.class);

    private DataSource dataSource;

    @Autowired
    public SqliteRestService(final DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public InputStreamResource exportDatabase(String projectKey) {
        File file;
        try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement();) {
            String location = System.getProperty("java.io.tmpdir") + "backUp.db";
            statement.executeUpdate("backup to " + location);
            file = new File(location);
        } catch (SQLException e) {
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST)
                    .message(new AiaApiMessage(AiaApiMessages.GENERAL.SQL_ERROR))
                    .originalException(e);
        }
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.INTERNAL_SERVER_ERROR)
                    .message(new AiaApiMessage(AiaApiMessages.GENERAL.FAILED_TO_CREATE_EXPORT_FILE))
                    .originalException(e);
        }
        return new InputStreamResource(new ByteArrayInputStream(bytes));
    }

    public ResultSetDTO executeQuery(String projectKey, String query) {
        ResultSetDTO resultSetDto = new ResultSetDTO();
        try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery(query)) {
            JSONArray jArray = mapResultSet(result);
            resultSetDto.setResultSet(jArray);
        } catch (SQLException e) {
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST)
                    .message(new AiaApiMessage(AiaApiMessages.GENERAL.SQL_ERROR))
                    .originalException(e);
        }
        return resultSetDto;
    }

    public void executeUpdate(String projectKey, String query) {
        try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement();) {
            statement.executeUpdate(query);
            LOGGER.info("Query: " + query + " executed successfully ");
        } catch (SQLException e) {
            LOGGER.info("Query: " + query + " FAILED ON EXECUTION!");
            throw new AiaApiException()
                    .statusCode(AiaApiException.AiaApiHttpCodes.BAD_RERQUEST)
                    .message(new AiaApiMessage(AiaApiMessages.GENERAL.SQL_ERROR))
                    .originalException(e);
        }
    }

    private static JSONArray mapResultSet(ResultSet rs) throws SQLException {
        JSONArray jArray = new JSONArray();
        JSONObject jsonObject = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (rs.next()) {
            jsonObject = new JSONObject();
            for (int index = 1; index <= columnCount; index++) {
                String column = rsmd.getColumnName(index);
                Object value = rs.getObject(column);
                if (value == null) {
                    jsonObject.put(column, "");
                } else if (value instanceof Integer) {
                    jsonObject.put(column, (Integer) value);
                } else if (value instanceof String) {
                    jsonObject.put(column, (String) value);
                } else if (value instanceof Boolean) {
                    jsonObject.put(column, (Boolean) value);
                } else if (value instanceof Date) {
                    jsonObject.put(column, ((Date) value).getTime());
                } else if (value instanceof Long) {
                    jsonObject.put(column, (Long) value);
                } else if (value instanceof Double) {
                    jsonObject.put(column, (Double) value);
                } else if (value instanceof Float) {
                    jsonObject.put(column, (Float) value);
                } else if (value instanceof BigDecimal) {
                    jsonObject.put(column, (BigDecimal) value);
                } else if (value instanceof Byte) {
                    jsonObject.put(column, (Byte) value);
                } else if (value instanceof byte[]) {
                    jsonObject.put(column, (byte[]) value);
                } else {
                    throw new IllegalArgumentException("object type can not be mapped: " + value.getClass());
                }
            }
            jArray.add(jsonObject);
        }
        return jArray;
    }
}
