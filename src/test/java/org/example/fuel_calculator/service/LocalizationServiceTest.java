package org.example.fuel_calculator.service;

import org.example.fuel_calculator.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalizationServiceTest {

    @BeforeEach
    void resetStaticState() throws Exception {
        getCache().clear();
        setCurrentLanguage("en");
    }

    @Test
    void getString_returnsValueFromCurrentLanguage() throws Exception {
        Map<String, String> fr = new HashMap<>();
        fr.put("title", "Bonjour");
        getCache().put("fr", fr);
        setCurrentLanguage("fr");

        assertEquals("Bonjour", LocalizationService.getString("title"));
    }

    @Test
    void getString_fallsBackToEnglishWhenKeyMissingInCurrentLanguage() throws Exception {
        getCache().put("fr", new HashMap<>());

        Map<String, String> en = new HashMap<>();
        en.put("title", "Hello");
        getCache().put("en", en);

        setCurrentLanguage("fr");

        assertEquals("Hello", LocalizationService.getString("title"));
    }

    @Test
    void getString_returnsBracketedKeyWhenMissingEverywhere() throws Exception {
        getCache().put("en", new HashMap<>());
        setCurrentLanguage("ja");

        assertEquals("[unknown_key]", LocalizationService.getString("unknown_key"));
    }

    @Test
    void loadStrings_usesCacheAndDoesNotNeedDatabaseWhenLanguageAlreadyCached() throws Exception {
        Map<String, String> cached = new HashMap<>();
        cached.put("title", "Cached title");
        getCache().put("fr", cached);

        assertDoesNotThrow(() -> LocalizationService.loadStrings("fr"));
        assertEquals("fr", LocalizationService.getCurrentLanguage());
        assertEquals("Cached title", LocalizationService.getString("title"));
    }

    @Test
    void loadStrings_loadsFromDatabaseAndPopulatesCache() throws Exception {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);
        ResultSet rs = Mockito.mock(ResultSet.class);

        Mockito.when(conn.prepareStatement(Mockito.anyString())).thenReturn(stmt);
        Mockito.when(stmt.executeQuery()).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(true, true, false);
        Mockito.when(rs.getString("key")).thenReturn("title", "calculate");
        Mockito.when(rs.getString("value")).thenReturn("Hello", "Calculate");

        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);

            LocalizationService.loadStrings("en");
        }

        assertEquals("en", LocalizationService.getCurrentLanguage());
        assertEquals("Hello", LocalizationService.getString("title"));
        assertEquals("Calculate", LocalizationService.getString("calculate"));
    }

    @Test
    void loadStrings_handlesDatabaseFailureWithoutThrowing() {
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("boom"));

            assertDoesNotThrow(() -> LocalizationService.loadStrings("fr"));
            assertEquals("fr", LocalizationService.getCurrentLanguage());
            assertEquals("[title]", LocalizationService.getString("title"));
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, String>> getCache() throws Exception {
        Field cacheField = LocalizationService.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        return (Map<String, Map<String, String>>) cacheField.get(null);
    }

    private static void setCurrentLanguage(String language) throws Exception {
        Field currentLanguageField = LocalizationService.class.getDeclaredField("currentLanguage");
        currentLanguageField.setAccessible(true);
        currentLanguageField.set(null, language);
    }
}