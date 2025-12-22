package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.config.DBConnection;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.service.AuthService;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.regex.Pattern;

public class LoginController {

    // --- Containers ---
    @FXML private StackPane formsContainer;
    @FXML private VBox loginForm;
    @FXML private VBox registerForm;

    // --- Login Fields ---
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginPasswordFieldVisible;
    @FXML private CheckBox showLoginPasswordCheckbox;
    @FXML private ComboBox<String> loginRoleComboBox;
    @FXML private Label loginErrorLabel;
    @FXML private Label dbStatusLabel;

    // --- Register Fields ---
    @FXML private TextField regFullNameField;
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regPasswordFieldVisible;
    @FXML private CheckBox showRegPasswordCheckbox;
    @FXML private TextField regPhoneField;
    @FXML private TextField regEmployeeIdField;
    @FXML private ComboBox<String> regRoleComboBox;
    @FXML private ComboBox<String> regSexComboBox;
    @FXML private ComboBox<String> regShiftComboBox;
    @FXML private ComboBox<String> regDepartmentComboBox;
    @FXML private Label regErrorLabel;

    private AuthService authService;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @FXML
    public void initialize() {
        authService = new AuthService();

        // Setup Login Role
        loginRoleComboBox.getItems().addAll("USER", "ADMIN");
        loginRoleComboBox.setValue("USER");

        // Setup Register Combos
        setupRegisterComboBoxes();

        // Setup Password Visibility Toggles
        setupPasswordVisibility(loginPasswordField, loginPasswordFieldVisible, showLoginPasswordCheckbox);
        setupPasswordVisibility(regPasswordField, regPasswordFieldVisible, showRegPasswordCheckbox);

        // Check DB connection
        checkDbConnection();
        
        // Ensure Login is shown first
        showLoginView();
    }

    private void setupRegisterComboBoxes() {
        regRoleComboBox.getItems().addAll("USER", "ADMIN");
        regSexComboBox.getItems().addAll("Male", "Female");
        regShiftComboBox.getItems().addAll("Day", "Night", "Flexible");
        regDepartmentComboBox.getItems().addAll(
                "Human Resources", "Information Technology", "Finance",
                "Operations", "Marketing", "Sales", "Research & Development"
        );

        // Set defaults
        regRoleComboBox.setValue("USER");
        regSexComboBox.setValue("Male");
        regShiftComboBox.setValue("Day");
        regDepartmentComboBox.setValue("Information Technology");
    }

    private void setupPasswordVisibility(PasswordField passField, TextField textField, CheckBox checkBox) {
        // Bind text properties
        textField.managedProperty().bind(checkBox.selectedProperty());
        textField.visibleProperty().bind(checkBox.selectedProperty());
        
        passField.managedProperty().bind(checkBox.selectedProperty().not());
        passField.visibleProperty().bind(checkBox.selectedProperty().not());

        // Sync text
        textField.textProperty().bindBidirectional(passField.textProperty());
    }

    private void checkDbConnection() {
        if (DBConnection.testConnection()) {
            dbStatusLabel.setText("Database: Connected ✅");
            dbStatusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else {
            dbStatusLabel.setText("Database: Disconnected ❌");
            dbStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }

    // --- Navigation ---

    @FXML
    private void showRegisterView() {
        animateTransition(loginForm, registerForm);
    }

    @FXML
    private void showLoginView() {
        animateTransition(registerForm, loginForm);
    }

    private void animateTransition(VBox from, VBox to) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), from);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            from.setVisible(false);
            to.setVisible(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), to);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    // --- Login Logic ---

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();
        String role = loginRoleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter username and password");
            return;
        }

        loginErrorLabel.setVisible(false);

        User user = authService.authenticate(username, password);

        if (user != null) {
            if (user.getRole().equalsIgnoreCase(role)) {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    SceneSwitcher.switchToAdminDashboard();
                } else {
                    SceneSwitcher.switchToUserDashboard();
                }
            } else {
                showLoginError("Invalid role for this user");
            }
        } else {
            showLoginError("Invalid username or password");
        }
    }

    private void showLoginError(String message) {
        loginErrorLabel.setText(message);
        loginErrorLabel.setVisible(true);
    }

    // --- Register Logic ---

    @FXML
    private void handleRegister() {
        if (validateRegisterInput()) {
            User user = createUserFromInput();

            if (authService.registerUser(user)) {
                showRegisterSuccess("Registration successful! Please login.");
                clearRegisterForm();
                // Optional: Auto switch to login after delay
            } else {
                showRegisterError("Registration failed. Username may already exist.");
            }
        }
    }

    private boolean validateRegisterInput() {
        if (regFullNameField.getText().trim().isEmpty()) {
            showRegisterError("Full name is required");
            return false;
        }

        if (!USERNAME_PATTERN.matcher(regUsernameField.getText()).matches()) {
            showRegisterError("Username must be 3-20 chars (letters, numbers, _)");
            return false;
        }

        if (regPasswordField.getText().length() < 6) {
            showRegisterError("Password must be at least 6 characters");
            return false;
        }

        String phone = regPhoneField.getText().trim();
        if (phone.isEmpty()) {
            showRegisterError("Phone number is mandatory");
            return false;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            showRegisterError("Invalid phone format");
            return false;
        }

        return true;
    }

    private User createUserFromInput() {
        User user = new User();
        user.setFullName(regFullNameField.getText().trim());
        user.setUsername(regUsernameField.getText().trim());
        user.setPassword(regPasswordField.getText());
        user.setPhoneNumber(regPhoneField.getText().trim());
        user.setEmployeeId(regEmployeeIdField.getText().trim().isEmpty() ? null : regEmployeeIdField.getText().trim());
        user.setRole(regRoleComboBox.getValue());
        user.setSex(regSexComboBox.getValue());
        user.setShift(regShiftComboBox.getValue());
        user.setDepartmentName(regDepartmentComboBox.getValue());
        return user;
    }

    private void showRegisterError(String message) {
        regErrorLabel.setText(message);
        regErrorLabel.setStyle("-fx-text-fill: #e74c3c;");
        regErrorLabel.setVisible(true);
    }

    private void showRegisterSuccess(String message) {
        regErrorLabel.setText(message);
        regErrorLabel.setStyle("-fx-text-fill: #2ecc71;");
        regErrorLabel.setVisible(true);
    }

    private void clearRegisterForm() {
        regFullNameField.clear();
        regUsernameField.clear();
        regPasswordField.clear();
        regPhoneField.clear();
        regEmployeeIdField.clear();
        regRoleComboBox.setValue("USER");
        regSexComboBox.setValue("Male");
        regShiftComboBox.setValue("Day");
        regDepartmentComboBox.setValue("Information Technology");
    }
}