package com.amdocs.aia.il.sqlite.service.unit;

import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.sqlite.config.AutoConfiguration;
import com.amdocs.aia.il.sqlite.config.DataSourceConfiguration;
import com.amdocs.aia.il.sqlite.dto.ResultSetDTO;
import com.amdocs.aia.il.sqlite.service.SqliteRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {AutoConfiguration.class, DataSourceConfiguration.class})
public class SqliteRestServiceUnitTest {

    @Spy
    private SQLiteDataSource dataSource = new SQLiteDataSource();

    @InjectMocks
    private SqliteRestService sqliteRestService;

    @Mock
    private MessageSource messageSource;

    private final String dbUrl = "jdbc:sqlite:unitTestDb";

    @BeforeEach
    void setUp() {
        dataSource.setUrl(dbUrl);
        try (Connection conn = dataSource.getConnection(); Statement statement = conn.createStatement();) {
            statement.executeUpdate("drop table if exists contacts");
            statement.executeUpdate("CREATE TABLE contacts (contact_id INTEGER PRIMARY KEY,first_name TEXT);");
            statement.executeUpdate("insert into contacts select 1 as contact_id, 'led' as first_name UNION ALL SELECT 2, 'styx' UNION ALL SELECT 3, 'tool';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void whenExecuteQuery_shouldReturnResultSet() {
        ResultSetDTO dto = sqliteRestService.executeQuery("projectKey", "select * from contacts");
        assertNotNull(dto);
    }

    @Test
    void whenExecuteUpdateWithWrongQuery_shouldThrowException() {
        assertThrows(AiaApiException.class,
                () -> sqliteRestService.executeUpdate("projectKey", "select from contacts")
        );
    }

}
