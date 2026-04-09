package org.example.fuel_calculator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DatabaseConnectionTest {

    @AfterEach
    void clearProperties() {
        System.clearProperty("db.url");
        System.clearProperty("db.user");
        System.clearProperty("db.password");
        System.clearProperty("test.prop");
    }

    @Test
    void getConnection_throwsWhenRequiredPasswordMissing() {
        System.setProperty("db.url", "jdbc:mysql://localhost:1/test");
        System.setProperty("db.user", "root");
        System.clearProperty("db.password");

        IllegalStateException ex = assertThrows(IllegalStateException.class, DatabaseConnection::getConnection);
        assertEquals(true, ex.getMessage().contains("DB_PASSWORD"));
    }

    @Test
    void getConnection_usesPropertiesAndFailsWithSqlExceptionOnInvalidDriver() {
        System.setProperty("db.url", "jdbc:invalid://localhost/test");
        System.setProperty("db.user", "root");
        System.setProperty("db.password", "secret");

        assertThrows(SQLException.class, DatabaseConnection::getConnection);
    }

    @Test
    void readConfig_usesPropertyWhenNoEnvironmentValue() throws Exception {
        System.setProperty("test.prop", "value-from-property");

        String value = invokeReadConfig("__ENV_DOES_NOT_EXIST__", "test.prop", "default", false);

        assertEquals("value-from-property", value);
    }

    @Test
    void readConfig_usesDefaultWhenPropertyBlankAndNotRequired() throws Exception {
        System.setProperty("test.prop", "   ");

        String value = invokeReadConfig("__ENV_DOES_NOT_EXIST__", "test.prop", "default", false);

        assertEquals("default", value);
    }

    @Test
    void readConfig_throwsWhenRequiredAndNothingProvided() {
        System.clearProperty("test.prop");

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> invokeReadConfig("__ENV_DOES_NOT_EXIST__", "test.prop", null, true));

        assertEquals(true, ex.getMessage().contains("test.prop"));
    }

    private static String invokeReadConfig(String envKey, String propertyKey, String defaultValue, boolean required) throws Exception {
        Method method = DatabaseConnection.class.getDeclaredMethod("readConfig", String.class, String.class, String.class, boolean.class);
        method.setAccessible(true);
        try {
            return (String) method.invoke(null, envKey, propertyKey, defaultValue, required);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            if (ex.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw ex;
        }
    }
}

