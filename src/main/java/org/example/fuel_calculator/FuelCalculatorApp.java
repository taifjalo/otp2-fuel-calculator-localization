package org.example.fuel_calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class FuelCalculatorApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        URL fxmlUrl = getClass().getResource("/org/example/fuel_calculator/fuel-calculator-view.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        VBox root = loader.load();

        // Create scene first
        Scene scene = new Scene(root, 500, 600);

        // Then add CSS
        scene.getStylesheets().add(getClass().getResource("/org/example/fuel_calculator/style.css").toExternalForm());

        primaryStage.setTitle("Fuel Consumption Calculator - LTR/RTL Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
