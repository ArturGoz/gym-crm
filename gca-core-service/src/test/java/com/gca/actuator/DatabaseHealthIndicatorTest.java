package com.gca.actuator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @InjectMocks
    private DatabaseHealthIndicator indicator;

    @Test
    void shouldReturnUpWhenDatabaseIsValid() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(true);

        Health actual = indicator.health();

        assertEquals("UP", actual.getStatus().getCode());
        assertEquals("Available", actual.getDetails().get("Database"));
        verify(connection, times(1)).close();
    }

    @Test
    void shouldReturnDownWhenDatabaseIsInvalid() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(1)).thenReturn(false);

        Health actual = indicator.health();

        assertEquals("DOWN", actual.getStatus().getCode());
        assertEquals("Not responding", actual.getDetails().get("Database"));
        verify(connection, times(1)).close();
    }

    @Test
    void shouldReturnDownWhenSQLExceptionOccurs() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB Connection failed"));

        Health actual = indicator.health();

        assertEquals("DOWN", actual.getStatus().getCode());
        assertEquals("Unavailable", actual.getDetails().get("Database"));
        assertEquals("DB Connection failed", actual.getDetails().get("Error"));
    }
}