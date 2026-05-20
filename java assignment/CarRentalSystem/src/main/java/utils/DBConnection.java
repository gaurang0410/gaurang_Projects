package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnection handles the database connection logic.
 * Simple, production-style implementation.
 */
public class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);
    private static final String PROPERTIES_FILE = "db.properties";
    private static final HikariDataSource DATA_SOURCE;

    static {
        Properties properties = new Properties();
        try (InputStream inputStream = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing required database config file: " + PROPERTIES_FILE);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read database config file: " + PROPERTIES_FILE, e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getRequiredProperty(properties, "db.url"));
        config.setUsername(getRequiredProperty(properties, "db.username"));
        config.setPassword(getRequiredProperty(properties, "db.password"));
        config.setDriverClassName(properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
        config.setMaximumPoolSize(getIntProperty(properties, "db.pool.maximumPoolSize", 20));
        config.setMinimumIdle(getIntProperty(properties, "db.pool.minimumIdle", 5));
        config.setConnectionTimeout(getLongProperty(properties, "db.pool.connectionTimeoutMs", 30000L));
        config.setIdleTimeout(getLongProperty(properties, "db.pool.idleTimeoutMs", 600000L));
        config.setMaxLifetime(getLongProperty(properties, "db.pool.maxLifetimeMs", 1800000L));
        config.setLeakDetectionThreshold(getLongProperty(properties, "db.pool.leakDetectionThresholdMs", 20000L));
        config.setPoolName(properties.getProperty("db.pool.name", "CarRentalPool"));

        DATA_SOURCE = new HikariDataSource(config);
        logger.info("Initialized database connection pool: {}", config.getPoolName());
        
        // Auto-update DB schema for new booking workflow
        try (Connection conn = DATA_SOURCE.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE bookings MODIFY status ENUM('PENDING', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'PAYMENT_PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'PENDING_APPROVAL'");
        } catch (SQLException e) {
            logger.warn("Could not alter bookings status enum (might already be updated): {}", e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            return DATA_SOURCE.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }

    private static String getRequiredProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value.trim();
    }

    private static int getIntProperty(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    private static long getLongProperty(Properties properties, String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Long.parseLong(value.trim());
    }
}
