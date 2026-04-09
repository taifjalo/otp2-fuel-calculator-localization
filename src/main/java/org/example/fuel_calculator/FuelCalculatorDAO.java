package org.example.fuel_calculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FuelCalculatorDAO {

    // ✅ Fixed: column names match the schema exactly (total_fuel, total_cost)
    @SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
    private static final String INSERT_SQL =
            "INSERT INTO calculation_records " +
                    "(distance, consumption, price, total_fuel, total_cost, language) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    /**
     * Saves one calculation result to the database.
     * Uses PreparedStatement to prevent SQL injection.
     *
     * @param distance    km entered by user
     * @param consumption L/100km entered by user
     * @param price       €/L entered by user
     * @param totalFuel   calculated fuel needed (L)
     * @param totalCost   calculated total cost (€)
     * @param language    active language code e.g. "en", "fr"
     */
    public static void saveRecord(
            double distance,
            double consumption,
            double price,
            double totalFuel,
            double totalCost,
            String language
    ) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) {

            stmt.setDouble(1, distance);
            stmt.setDouble(2, consumption);
            stmt.setDouble(3, price);
            stmt.setDouble(4, totalFuel);
            stmt.setDouble(5, totalCost);
            stmt.setString(6, language);

            int rows = stmt.executeUpdate();
            Logger.getGlobal().log(Level.INFO, "Saved to DB. Rows inserted: {0}", rows);

        } catch (SQLException e) {
            Logger.getGlobal().log(Level.INFO, "DB save failed: {0}", e.getMessage());
        }
    }

    private FuelCalculatorDAO() {
        // Utility class
    }
}