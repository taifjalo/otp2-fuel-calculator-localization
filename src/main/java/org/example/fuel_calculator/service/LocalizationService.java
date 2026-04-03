package org.example.fuel_calculator.service;

import org.example.fuel_calculator.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads UI strings from the localization_strings database table.
 *
 * WEEK 3 CHANGE: No longer reads from .properties files.
 * Instead queries: SELECT `key`, value FROM localization_strings WHERE language = ?
 *
 * Strings are cached after first load — switching back to a language
 * that was already used makes no DB call.
 */
public class LocalizationService {

    // Cache: language code → { key → value }
    // e.g.  "en" → { "title" → "Fuel Calculator", "calculate" → "Calculate" }
    private static final Map<String, Map<String, String>> cache = new HashMap<>();

    private static String currentLanguage = "en";

    /**
     * Loads all strings for the given language from the DB.
     * Caches results so the DB is only queried once per language.
     *
     * @param language  e.g. "en", "fr", "ja", "fa"
     */
    public static void loadStrings(String language) {
        currentLanguage = language;

        // Already cached — skip DB
        if (cache.containsKey(language)) {
            System.out.println("Using cached strings for: " + language);
            return;
        }

        Map<String, String> strings = new HashMap<>();
        String sql = "SELECT `key`, value FROM localization_strings WHERE language = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, language);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                strings.put(rs.getString("key"), rs.getString("value"));
            }

            cache.put(language, strings);
            System.out.println("Loaded " + strings.size() + " strings for: " + language);

        } catch (SQLException e) {
            System.err.println("DB error loading strings for '" + language + "': " + e.getMessage());
        }
    }

    /**
     * Returns the localized string for the given key.
     * Falls back to English if not found in current language.
     *
     * @param key  e.g. "title", "calculate", "fuel_result"
     */
    public static String getString(String key) {
        Map<String, String> strings = cache.getOrDefault(currentLanguage, Collections.emptyMap());
        if (strings.containsKey(key)) {
            return strings.get(key);
        }
        // Fallback: English
        Map<String, String> fallback = cache.getOrDefault("en", Collections.emptyMap());
        return fallback.getOrDefault(key, "[" + key + "]");
    }

    /**
     * Returns the currently active language code.
     */
    public static String getCurrentLanguage() {
        return currentLanguage;
    }
}