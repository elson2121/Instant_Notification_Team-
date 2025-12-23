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
    
    // --- Registration Steps ---
    @FXML private VBox regStep1;
    @FXML private VBox regStep2;
    @FXML private Label regStepLabel;

    // --- Login Fields ---
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginPasswordFieldVisible;
    @FXML private CheckBox showLoginPasswordCheckbox;
    @FXML private ComboBox<String> loginRoleComboBox;
    @FXML private Label loginErrorLabel;

    // --- Register Fields (Step 1) ---
    @FXML private TextField regFullNameField;
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regPasswordFieldVisible;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private TextField regConfirmPasswordFieldVisible;
    @FXML private TextField regPhoneField;
    
    // --- Register Fields (Step 2) ---
    @FXML private TextField regEmployeeIdField;
    @FXML private ComboBox<String> regRoleComboBox;
    @FXML private ComboBox<String> regSexComboBox;
    @FXML private ComboBox<String> regShiftComboBox;
    @FXML private ComboBox<String> regDepartmentComboBox;
    
    @FXML private CheckBox showRegPasswordCheckbox;
    @FXML private Label regErrorLabel;

    private AuthService authService;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+251|0)(9|7)\\d{8}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern FULLNAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{3,50}$");

    @FXML
    public void initialize() {
        authService = new AuthService();

        // Setup Login Role
        if (loginRoleComboBox != null) {
            loginRoleComboBox.getItems().addAll("USER", "ADMIN");
            loginRoleComboBox.setValue("USER");
        }

        // Setup Register Combos
        setupRegisterComboBoxes();

        // Setup Password Visibility
        setupPasswordVisibility(loginPasswordField, loginPasswordFieldVisible, showLoginPasswordCheckbox);
        setupPasswordVisibility(regPasswordField, regPasswordFieldVisible, showRegPasswordCheckbox);
        setupPasswordVisibility(regConfirmPasswordField, regConfirmPasswordFieldVisible, showRegPasswordCheckbox);

        // Ensure Login is shown first
        showLoginView();
    }

    private void setupRegisterComboBoxes() {
        regRoleComboBox.getItems().addAll("User", "Admin");
        regSexComboBox.getItems().addAll("Male", "Female");
        regShiftComboBox.getItems().addAll("Morning", "Afternoon", "Night", "Rotating");
        regDepartmentComboBox.getItems().addAll(
                "IT", "Human Resources", "Finance",
                "Operations", "Marketing", "Sales"
        );
    }

    private void setupPasswordVisibility(PasswordField passField, TextField textField, CheckBox checkBox) {
        if (passField == null || textField == null || checkBox == null) return;
        
        textField.managedProperty().bind(checkBox.selectedProperty());
        textField.visibleProperty().bind(checkBox.selectedProperty());
        
        passField.managedProperty().bind(checkBox.selectedProperty().not());
        passField.visibleProperty().bind(checkBox.selectedProperty().not());

        textField.textProperty().bindBidirectional(passField.textProperty());
    }

    // --- Navigation ---

    @FXML
    private void showRegisterView() {
        animateTransition(loginForm, registerForm);
        resetRegisterSteps();
    }

    @FXML
    private void showLoginView() {
        animateTransition(registerForm, loginForm);
    }
    
    private void resetRegisterSteps() {
        regStep1.setVisible(true);
        regStep2.setVisible(false);
        regStepLabel.setText("Step 1 of 2");
        regErrorLabel.setVisible(false);
    }

    @FXML
    private void handleNextStep() {
        // Bypass validation for Step 1 as requested
        // if (validateStep1()) {
            regStep1.setVisible(false);
            regStep2.setVisible(true);
            regStepLabel.setText("Step 2 of 2");
            regErrorLabel.setVisible(false);
        // }
    }

    @FXML
    private void handlePrevStep() {
        regStep2.setVisible(false);
        regStep1.setVisible(true);
        regStepLabel.setText("Step 1 of 2");
        regErrorLabel.setVisible(false);
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
        String role = (loginRoleComboBox != null && loginRoleComboBox.getValue() != null) ? loginRoleComboBox.getValue() : "USER";

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter username and password");
            return;
        }

        loginErrorLabel.setVisible(false);

        User user = authService.authenticate(username, password);

        if (user != null) {
            // Check role (case-insensitive)
            if (loginRoleComboBox == null || user.getRole().equalsIgnoreCase(role)) {
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
        if (validateStep2()) {
            User user = createUserFromInput();

            if (authService.registerUser(user)) {
                showRegisterSuccess("Registration successful! Please login.");
                clearRegisterForm();
            } else {
                showRegisterError("Registration failed. Username may already exist.");
            }
        }
    }

    private boolean validateStep1() {
        // Full Name
        String fullName = regFullNameField.getText().trim();
        if (fullName.isEmpty()) {
            showRegisterError("Full name is required");
            return false;
        }
        if (!FULLNAME_PATTERN.matcher(fullName).matches()) {
            showRegisterError("Full Name must contain only letters and spaces (min 3 chars)");
            return false;
        }

        // Username
        if (!USERNAME_PATTERN.matcher(regUsernameField.getText()).matches()) {
            showRegisterError("Username must be 3-20 chars (letters, numbers, _)");
            return false;
        }

        // Password
        String password = regPasswordField.getText();
        if (password.length() < 6) {
            showRegisterError("Password must be at least 6 characters");
            return false;
        }
        
        // Confirm Password
        if (!password.equals(regConfirmPasswordField.getText())) {
            showRegisterError("Passwords do not match");
            return false;
        }

        // Phone
        String phone = regPhoneField.getText().trim();
        if (phone.isEmpty()) {
            showRegisterError("Phone number is required");
            return false;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            showRegisterError("Invalid Ethiopian phone (e.g., 0911... or +2519...)");
            return false;
        }

        return true;
    }
    
    private boolean validateStep2() {
        if (regEmployeeIdField.getText().trim().isEmpty()) {
            showRegisterError("Employee ID is required");
            return false;
        }
        if (regRoleComboBox.getValue() == null) {
            showRegisterError("Role is required");
            return false;
        }
        if (regSexComboBox.getValue() == null) {
            showRegisterError("Sex is required");
            return false;
        }
        if (regShiftComboBox.getValue() == null) {
            showRegisterError("Shift is required");
            return false;
        }
        if (regDepartmentComboBox.getValue() == null) {
            showRegisterError("Department is required");
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
        user.setEmployeeId(regEmployeeIdField.getText().trim());
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
        regConfirmPasswordField.clear();
        regPhoneField.clear();
        regEmployeeIdField.clear();
        regRoleComboBox.setValue("User");
        regSexComboBox.setValue("Male");
        regShiftComboBox.setValue("Morning");
        regDepartmentComboBox.setValue("IT");
        resetRegisterSteps();
    }
}