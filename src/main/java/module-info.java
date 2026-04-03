module main.java.otp2fuelcalculatorlocalization {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.fuel_calculator to javafx.fxml;
    exports org.example.fuel_calculator;
}