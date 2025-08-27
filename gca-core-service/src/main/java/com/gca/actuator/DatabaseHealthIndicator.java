package com.gca.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final String DATABASE_KEY = "Database";

    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up().withDetail(DATABASE_KEY, "Available").build();
            }

            return Health.down().withDetail(DATABASE_KEY, "Not responding").build();
        } catch (SQLException e) {
            return Health.down()
                    .withDetail(DATABASE_KEY, "Unavailable")
                    .withDetail("Error", e.getMessage())
                    .build();
        }
    }
}
