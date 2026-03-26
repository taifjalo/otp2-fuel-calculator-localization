module main.java.otp2fuelcalculatorlocalization {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.fuel_calculator to javafx.fxml;
    exports org.example.fuel_calculator;
}