package org.example.fuel_calculator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://host.docker.internal:3306/fuel_calculator_localization?useUnicode=true&characterEncoding=UTF-8";
    private static final String DEFAULT_USER = "root";

    private DatabaseConnection() {
        // Utility class
    }

    /**
     * Opens and returns a new database connection.
     * The caller is responsible for closing the connection.
     */
    public static Connection getConnection() throws SQLException {
        String url = readConfig("DB_URL", "db.url", DEFAULT_URL, false);
        String user = readConfig("DB_USER", "db.user", DEFAULT_USER, false);
        String password = readConfig("DB_PASSWORD", "db.password", null, true);
        return DriverManager.getConnection(url, user, password);
    }

    private static String readConfig(String envKey, String propertyKey, String defaultValue, boolean required) {
        String value = System.getenv(envKey);
        if (isBlank(value)) {
            value = System.getProperty(propertyKey);
        }
        if (isBlank(value)) {
            value = defaultValue;
        }
        if (required && isBlank(value)) {
            throw new IllegalStateException(
                    "Missing required database configuration. Set env var '" + envKey
                            + "' or JVM property '-D" + propertyKey + "'."
            );
        }
        return value;
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
