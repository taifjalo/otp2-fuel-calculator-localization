package org.example.fuel_calculator;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.fuel_calculator.service.LocalizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuelCalculatorControllerTest {

    private FuelCalculatorController controller;
    private Label lblTitle;
    private Label lblDistance;
    private Label lblFuel;
    private Label lblResult;
    private TextField tfDistance;
    private TextField tfFuel;
    private Button btnCalculate;

    @BeforeAll
    static void initJavaFxToolkit() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException ignored) {
            latch.countDown();
        }
        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    @BeforeEach
    void setUp() throws Exception {
        runOnFxThreadAndWait(() -> {
            controller = new FuelCalculatorController();
            lblTitle = new Label();
            lblDistance = new Label();
            lblFuel = new Label();
            lblResult = new Label();
            tfDistance = new TextField();
            tfFuel = new TextField();
            btnCalculate = new Button();
        });

        setField("rootVBox", new VBox());
        setField("lblTitle", lblTitle);
        setField("lblDistance", lblDistance);
        setField("lblFuel", lblFuel);
        setField("lblResult", lblResult);
        setField("tfDistance", tfDistance);
        setField("tfFuel", tfFuel);
        setField("btnCalculate", btnCalculate);

        resetLocalizationState();

        // Ensure DAO path throws SQLException (handled internally) instead of IllegalStateException.
        System.setProperty("db.url", "jdbc:mysql://localhost:1/fuel_calculator_localization");
        System.setProperty("db.user", "root");
        System.setProperty("db.password", "test");
    }

    @AfterEach
    void clearDbProperties() {
        System.clearProperty("db.url");
        System.clearProperty("db.user");
        System.clearProperty("db.password");
    }

    @Test
    void onCalculateClick_validInput_setsFormattedResult() throws Exception {
        Map<String, String> en = new HashMap<>();
        en.put("fuel_result", "Fuel %.1f Cost %.1f");
        setLanguageCache("en", en);
        setCurrentLanguage("en");

        runOnFxThreadAndWait(() -> {
            tfDistance.setText("200");
            tfFuel.setText("8");
            controller.onCalculateClick();
        });

        String expected = String.format("Fuel %.1f Cost %.1f", 16.0, 28.8);
        assertEquals(expected, lblResult.getText());
    }


    @ParameterizedTest
    @CsvSource({
            "-10,8",
            "abc,8",
            "10,0"
    })
    void onCalculateClick_invalidInputs_showLocalizedError(String distance, String fuel) throws Exception {
        Map<String, String> en = new HashMap<>();
        en.put("error_invalid_input", "Invalid input");
        setLanguageCache("en", en);
        setCurrentLanguage("en");

        runOnFxThreadAndWait(() -> {
            tfDistance.setText(distance);
            tfFuel.setText(fuel);
            controller.onCalculateClick();
        });

        assertEquals("Invalid input", lblResult.getText());
    }

    @Test
    void onFAClick_appliesRtlOrientationAndRightAlignment() throws Exception {
        Map<String, String> fa = new HashMap<>();
        fa.put("title", "T");
        fa.put("distance", "D");
        fa.put("fuel", "F");
        fa.put("calculate", "C");
        fa.put("enter_distance", "ED");
        fa.put("enter_fuel", "EF");
        setLanguageCache("fa", fa);

        runOnFxThreadAndWait(controller::onFAClick);
        waitForFxEvents();

        VBox root = (VBox) getField("rootVBox");
        assertEquals(NodeOrientation.RIGHT_TO_LEFT, root.getNodeOrientation());
        assertEquals(Pos.CENTER_RIGHT, tfDistance.getAlignment());
        assertEquals(Pos.CENTER_RIGHT, tfFuel.getAlignment());
    }

    @Test
    void initialize_withNullRootVBox_stillUpdatesFields() throws Exception {
        Map<String, String> en = new HashMap<>();
        en.put("title", "Title EN");
        en.put("distance", "Distance EN");
        en.put("fuel", "Fuel EN");
        en.put("calculate", "Calculate EN");
        en.put("enter_distance", "Enter distance EN");
        en.put("enter_fuel", "Enter fuel EN");
        setLanguageCache("en", en);

        setField("rootVBox", null);
        runOnFxThreadAndWait(controller::initialize);

        assertEquals("Title EN", lblTitle.getText());
        assertEquals(Pos.CENTER_LEFT, tfDistance.getAlignment());
        assertEquals(Pos.CENTER_LEFT, tfFuel.getAlignment());
    }

    @Test
    void initialize_andLanguageSwitch_updateLabelsAndPromptTexts() throws Exception {
        Map<String, String> en = new HashMap<>();
        en.put("title", "Title EN");
        en.put("distance", "Distance EN");
        en.put("fuel", "Fuel EN");
        en.put("calculate", "Calculate EN");
        en.put("enter_distance", "Enter distance EN");
        en.put("enter_fuel", "Enter fuel EN");
        setLanguageCache("en", en);

        Map<String, String> fr = new HashMap<>();
        fr.put("title", "Title FR");
        fr.put("distance", "Distance FR");
        fr.put("fuel", "Fuel FR");
        fr.put("calculate", "Calculate FR");
        fr.put("enter_distance", "Enter distance FR");
        fr.put("enter_fuel", "Enter fuel FR");
        setLanguageCache("fr", fr);

        runOnFxThreadAndWait(controller::initialize);
        runOnFxThreadAndWait(controller::onFRClick);
        waitForFxEvents();

        assertEquals("Title FR", lblTitle.getText());
        assertEquals("Distance FR", lblDistance.getText());
        assertEquals("Fuel FR", lblFuel.getText());
        assertEquals("Calculate FR", btnCalculate.getText());
        assertEquals("Enter distance FR", tfDistance.getPromptText());
        assertEquals("Enter fuel FR", tfFuel.getPromptText());
    }

    @Test
    void initialize_registersListenersThatClearPreviousResult() throws Exception {
        Map<String, String> en = new HashMap<>();
        en.put("title", "Title EN");
        en.put("distance", "Distance EN");
        en.put("fuel", "Fuel EN");
        en.put("calculate", "Calculate EN");
        en.put("enter_distance", "Enter distance EN");
        en.put("enter_fuel", "Enter fuel EN");
        setLanguageCache("en", en);

        runOnFxThreadAndWait(controller::initialize);

        runOnFxThreadAndWait(() -> {
            lblResult.setText("old result");
            tfDistance.setText("12");
        });

        assertEquals("", lblResult.getText());
    }

    private void setField(String name, Object value) throws Exception {
        Field field = FuelCalculatorController.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller, value);
    }

    private Object getField(String name) throws Exception {
        Field field = FuelCalculatorController.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(controller);
    }

    @SuppressWarnings("unchecked")
    private static void setLanguageCache(String language, Map<String, String> entries) throws Exception {
        Field cacheField = LocalizationService.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Map<String, Map<String, String>> cache = (Map<String, Map<String, String>>) cacheField.get(null);
        cache.put(language, entries);
    }

    @SuppressWarnings("unchecked")
    private static void resetLocalizationState() throws Exception {
        Field cacheField = LocalizationService.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        ((Map<String, Map<String, String>>) cacheField.get(null)).clear();
        setCurrentLanguage("en");
    }

    private static void setCurrentLanguage(String language) throws Exception {
        Field currentLanguageField = LocalizationService.class.getDeclaredField("currentLanguage");
        currentLanguageField.setAccessible(true);
        currentLanguageField.set(null, language);
    }

    private static void runOnFxThreadAndWait(Runnable action) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(3, TimeUnit.SECONDS));
    }

    private static void waitForFxEvents() throws Exception {
        runOnFxThreadAndWait(() -> { });
    }
}
