package org.example.fuel_calculator;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.fuel_calculator.service.LocalizationService;

public class FuelCalculatorController {

    //  FXML fields
    @FXML private VBox      rootVBox;
    @FXML private Label     lblTitle;
    @FXML private Label     lblDistance;
    @FXML private Label     lblFuel;
    @FXML private TextField tfDistance;
    @FXML private TextField tfFuel;
    @FXML private Button    btnCalculate;
    @FXML private Label     lblResult;

    // Lifecycle

    /**
     * Called automatically after FXML loading.
     * Loads English strings from DB on startup.
     */
    @FXML
    public void initialize() {
        setLanguage("en");

        tfDistance.textProperty().addListener((obs, o, n) -> lblResult.setText(""));
        tfFuel.textProperty().addListener((obs, o, n)     -> lblResult.setText(""));
    }

    // Language button handlers

    @FXML public void onENClick(ActionEvent e) { setLanguage("en"); }
    @FXML public void onFRClick(ActionEvent e) { setLanguage("fr"); }
    @FXML public void onJPClick(ActionEvent e) { setLanguage("ja"); }
    @FXML public void onFAClick(ActionEvent e) { setLanguage("fa"); }

    // Calculate button

    /**
     * 1. Validate input
     * 2. Calculate fuel consumption
     * 3. Show localized result (string from DB)
     * 4. Save record to calculation_records table via DAO
     */
    @FXML
    public void onCalculateClick(ActionEvent e) {
        try {
            double distance    = Double.parseDouble(tfDistance.getText().trim());
            double consumption = Double.parseDouble(tfFuel.getText().trim());

            if (distance <= 0 || consumption <= 0) {
                lblResult.setText(LocalizationService.getString("error_invalid_input"));
                return;
            }

            // fuel needed = (L/100km rate) * distance / 100
            double totalFuel = (consumption / 100.0) * distance;
            double price     = 1.80;  // default price — add tfPrice field if needed

            double totalCost = totalFuel * price;

            // Show result using pattern loaded from DB
            String pattern = LocalizationService.getString("fuel_result");
            lblResult.setText(String.format(pattern, totalFuel, totalCost));

            // ✅ Save to database via DAO
            FuelCalculatorDAO.saveRecord(
                    distance,
                    consumption,
                    price,
                    totalFuel,
                    totalCost,
                    LocalizationService.getCurrentLanguage()
            );

        } catch (NumberFormatException ex) {
            lblResult.setText(LocalizationService.getString("error_invalid_input"));
        }
    }

    // Private helpers

    /**
     * Loads strings from DB for the given language and updates all UI labels.
     */
    private void setLanguage(String language) {
        lblResult.setText("");

        // ✅ Load from DB (cached after first call)
        LocalizationService.loadStrings(language);

        // Update labels from DB strings
        lblTitle.setText(LocalizationService.getString("title"));
        lblDistance.setText(LocalizationService.getString("distance"));
        lblFuel.setText(LocalizationService.getString("fuel"));
        btnCalculate.setText(LocalizationService.getString("calculate"));
        tfDistance.setPromptText(LocalizationService.getString("enter_distance"));
        tfFuel.setPromptText(LocalizationService.getString("enter_fuel"));

        applyTextDirection(language);
    }

    /**
     * Applies RTL layout for Persian/Arabic, LTR for all others.
     * Same approach as the professor's averageSpeed_DB project.
     */
    private void applyTextDirection(String language) {
        boolean isRTL = language.equals("fa")
                || language.equals("ar")
                || language.equals("he")
                || language.equals("ur");

        Platform.runLater(() -> {
            if (rootVBox != null) {
                rootVBox.setNodeOrientation(
                        isRTL ? NodeOrientation.RIGHT_TO_LEFT
                                : NodeOrientation.LEFT_TO_RIGHT
                );
            }
            String style = isRTL
                    ? "-fx-text-alignment: right; -fx-alignment: center-right;"
                    : "-fx-text-alignment: left;  -fx-alignment: center-left;";
            tfDistance.setStyle(style);
            tfFuel.setStyle(style);
        });
    }
}