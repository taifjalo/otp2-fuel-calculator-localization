package org.example.fuel_calculator;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FuelCalculatorDAOTest {

    @Test
    void saveRecord_executesInsertWithExpectedParameters() throws Exception {
        Connection conn = Mockito.mock(Connection.class);
        PreparedStatement stmt = Mockito.mock(PreparedStatement.class);

        Mockito.when(conn.prepareStatement(Mockito.anyString())).thenReturn(stmt);
        Mockito.when(stmt.executeUpdate()).thenReturn(1);

        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenReturn(conn);

            FuelCalculatorDAO.saveRecord(100.0, 7.5, 1.8, 7.5, 13.5, "en");
        }

        Mockito.verify(stmt).setDouble(1, 100.0);
        Mockito.verify(stmt).setDouble(2, 7.5);
        Mockito.verify(stmt).setDouble(3, 1.8);
        Mockito.verify(stmt).setDouble(4, 7.5);
        Mockito.verify(stmt).setDouble(5, 13.5);
        Mockito.verify(stmt).setString(6, "en");
        Mockito.verify(stmt).executeUpdate();
    }

    @Test
    void saveRecord_handlesSqlExceptionWithoutThrowing() {
        try (MockedStatic<DatabaseConnection> dbMock = Mockito.mockStatic(DatabaseConnection.class)) {
            dbMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("db down"));

            assertDoesNotThrow(() -> FuelCalculatorDAO.saveRecord(10, 5, 1.8, 0.5, 0.9, "en"));
        }
    }
}

