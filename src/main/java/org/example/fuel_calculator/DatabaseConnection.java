package org.example.fuel_calculator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://host.docker.internal:3306/fuel_calculator_localization" + "?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER     = "root";
    private static final String PASSWORD = "taiftaif";

    /**
     * Opens and returns a new database connection.
     * The caller is responsible for closing the connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
