package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.Main;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField employeeIdField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private ComboBox<String> shiftComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private Label errorLabel;
    @FXML private CheckBox showPasswordCheckbox;
    @FXML private Button createAccountButton;
    @FXML private ScrollPane scrollPane;

    private UserDAO userDAO;

    // Professional email validation pattern
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    // Ethiopian phone pattern for Infobip
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+251)?[79]\\d{8}$");

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        setupComboBoxes();
        setupEventHandlers();
        setupPhoneFormatting();
        setupRealTimeValidation();

        // Apply initial green border styling
        applyGreenBorder(emailField);
        applyGreenBorder(phoneField);
        applyGreenBorder(employeeIdField);

        // Disable create account button by default
        createAccountButton.setDisable(true);
    }

    private void setupComboBoxes() {
        roleComboBox.getItems().addAll("Manager", "Employee", "Intern");
        sexComboBox.getItems().addAll("Male", "Female");
        shiftComboBox.getItems().addAll("Day", "Night");
        departmentComboBox.getItems().addAll("HR", "IT", "Sales", "Marketing");

        // Set default values
        roleComboBox.getSelectionModel().selectFirst();
        shiftComboBox.getSelectionModel().selectFirst();
        departmentComboBox.getSelectionModel().selectFirst();
    }

    private void setupEventHandlers() {
        // Real-time email validation
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmailInRealTime();
            validateForm();
        });

        // Real-time employee ID validation
        employeeIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });

        // Show password toggle
        showPasswordCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            togglePasswordVisibility(newValue);
        });

        // Enter key submission for all text fields
        fullNameField.setOnKeyPressed(this::handleEnterKey);
        usernameField.setOnKeyPressed(this::handleEnterKey);
        passwordField.setOnKeyPressed(this::handleEnterKey);
        confirmPasswordField.setOnKeyPressed(this::handleEnterKey);
        emailField.setOnKeyPressed(this::handleEnterKey);
        phoneField.setOnKeyPressed(this::handleEnterKey);
        employeeIdField.setOnKeyPressed(this::handleEnterKey);
    }

    private void setupPhoneFormatting() {
        // Auto-format phone for Infobip as user types
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePhoneInRealTime();
        });
    }

    private void setupRealTimeValidation() {
        // Full Name: No numbers allowed
        fullNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\d.*")) {
                setFieldErrorStyle(fullNameField);
            } else {
                applyGreenBorder(fullNameField);
            }
        });

        // Phone Number: Numeric only, with optional +251 prefix, starting with 9 or 7
        phoneField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("(\\+251)?[79]?\\d{0,8}")) {
                return change;
            }
            return null;
        }));
    }

    private void validateEmailInRealTime() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            setFieldErrorStyle(emailField);
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            setFieldWarningStyle(emailField);
            return;
        }

        // Note: Database check might be heavy for real-time validation
        // Consider doing this only on form submission or with debouncing
        applyGreenBorder(emailField);
    }

    private void validatePhoneInRealTime() {
        String phone = phoneField.getText().trim();

        if (phone.isEmpty()) {
            setFieldErrorStyle(phoneField);
            return;
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            setFieldWarningStyle(phoneField);
            return;
        }

        applyGreenBorder(phoneField);
    }

    private void applyGreenBorder(Control field) {
        field.setStyle(
                "-fx-border-color: #2a8c4a;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 8px;"
        );
    }

    private void setFieldErrorStyle(Control field) {
        field.setStyle(
                "-fx-border-color: red;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 8px;"
        );
    }

    private void setFieldWarningStyle(Control field) {
        field.setStyle(
                "-fx-border-color: #f39c12;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 8px;"
        );
    }

    private void togglePasswordVisibility(boolean show) {
        if (show) {
            // Show password by converting PasswordField to TextField behavior
            String password = passwordField.getText();
            passwordField.setPromptText(password);
            passwordField.setText("");

            String confirmPassword = confirmPasswordField.getText();
            confirmPasswordField.setPromptText(confirmPassword);
            confirmPasswordField.setText("");
        } else {
            // Hide password by restoring PasswordField behavior
            String password = passwordField.getPromptText();
            passwordField.setText(password);
            passwordField.setPromptText("Enter your password");

            String confirmPassword = confirmPasswordField.getPromptText();
            confirmPasswordField.setText(confirmPassword);
            confirmPasswordField.setPromptText("Confirm your password");
        }
    }

    private void handleEnterKey(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            handleRegister();
        }
    }

    @FXML
    private void handleRegister() {
        // Clear previous errors
        errorLabel.setVisible(false);

        // Get and trim field values
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = showPasswordCheckbox.isSelected() ?
                passwordField.getPromptText() : passwordField.getText().trim();
        String confirmPassword = showPasswordCheckbox.isSelected() ?
                confirmPasswordField.getPromptText() : confirmPasswordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String employeeId = employeeIdField.getText().trim();
        String role = roleComboBox.getValue();
        String sex = sexComboBox.getValue();
        String shift = shiftComboBox.getValue();
        String department = departmentComboBox.getValue();

        // Validate all required fields
        if (!validateAllFields(fullName, username, password, confirmPassword, email,
                phone, employeeId, role, shift, department)) {
            return;
        }

        // Enhanced email validation
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Invalid email format. Must contain '@' and '.' (e.g., user@example.com)");
            emailField.requestFocus();
            return;
        }

        // Check email uniqueness
        if (userDAO.isEmailExists(email)) {
            showError("Email address already registered. Please use a different email.");
            emailField.requestFocus();
            return;
        }

        // Check employee ID uniqueness
        if (userDAO.isEmployeeIdExists(employeeId)) {
            showError("Employee ID already registered. Please use a different ID.");
            employeeIdField.requestFocus();
            return;
        }

        // Format phone for Infobip
        String formattedPhone = formatPhoneForInfobip(phone);
        if (formattedPhone == null) {
            showError("Invalid phone number. Must be Ethiopian mobile (9 or 7 followed by 8 digits)\n" +
                    "Examples: 0912345678 or 912345678 or +251912345678");
            phoneField.requestFocus();
            return;
        }

        // Create user with Infobip-compatible phone
        User user = new User(0, fullName, username, password, role, department,
                formattedPhone, email, sex, shift, true, employeeId);

        try {
            // Insert into database
            if (userDAO.insertUser(user)) {
                showSuccessAndRedirect(username);
            } else {
                showError("Registration failed. Please try again.");
            }
        } catch (IllegalArgumentException e) {
            // Handle validation errors from DAO
            showError(e.getMessage());
        }
    }

    private boolean validateAllFields(String fullName, String username, String password,
                                      String confirmPassword, String email, String phone,
                                      String employeeId, String role, String shift, String department) {

        // Check required fields
        if (fullName.isEmpty()) {
            showError("Full Name is required");
            fullNameField.requestFocus();
            return false;
        }

        if (username.isEmpty()) {
            showError("Username is required");
            usernameField.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            showError("Password is required");
            passwordField.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            confirmPasswordField.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            showError("Email Address is required");
            emailField.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            showError("Phone Number is required");
            phoneField.requestFocus();
            return false;
        }

        if (employeeId.isEmpty()) {
            showError("Employee ID is required");
            employeeIdField.requestFocus();
            return false;
        }

        if (department == null) {
            showError("Please select Department");
            departmentComboBox.requestFocus();
            return false;
        }

        if (role == null) {
            showError("Please select Role");
            roleComboBox.requestFocus();
            return false;
        }

        if (shift == null) {
            showError("Please select Shift");
            shiftComboBox.requestFocus();
            return false;
        }

        // Password confirmation
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return false;
        }

        // Password strength
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            passwordField.requestFocus();
            return false;
        }

        // Username length
        if (username.length() < 3) {
            showError("Username must be at least 3 characters long");
            usernameField.requestFocus();
            return false;
        }

        // Full name validation
        if (fullName.length() < 2) {
            showError("Full name must be at least 2 characters long");
            fullNameField.requestFocus();
            return false;
        }

        // Scroll to top after validation
        if (scrollPane != null) {
            scrollPane.setVvalue(0.0);
        }

        return true;
    }

    private String formatPhoneForInfobip(String phone) {
        // Clean the phone number
        String cleanPhone = phone.replaceAll("[^\\d+]", "");

        // Already formatted with +251
        if (cleanPhone.startsWith("+251") && cleanPhone.length() == 13) {
            return cleanPhone;
        }

        // Starts with 0
        if (cleanPhone.startsWith("0") && cleanPhone.length() == 10) {
            return "+251" + cleanPhone.substring(1);
        }

        // Starts with 9 or 7 (Ethiopian mobile prefixes)
        if ((cleanPhone.startsWith("9") || cleanPhone.startsWith("7")) && cleanPhone.length() == 9) {
            return "+251" + cleanPhone;
        }

        return null; // Invalid format
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setTextFill(Color.web("#ef4444"));
        errorLabel.setStyle("-fx-background-color: #fee2e2; -fx-padding: 10; -fx-background-radius: 6;");
    }

    private void showSuccessAndRedirect(String username) {
        // Create custom success dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText("🎉 Account Created Successfully!");

        String content = String.format(
                "Account created for %s. SMS and Email notifications enabled.",
                username
        );

        alert.setContentText(content);

        // Add Infobip icon or styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2px;");

        alert.showAndWait();

        // Clear form
        clearForm();

        // Redirect to login
        handleBackToLogin();
    }

    private void clearForm() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        passwordField.setPromptText("Enter your password");
        confirmPasswordField.clear();
        confirmPasswordField.setPromptText("Confirm your password");
        emailField.clear();
        phoneField.clear();
        employeeIdField.clear();

        // Reset comboboxes to defaults
        roleComboBox.getSelectionModel().selectFirst();
        shiftComboBox.getSelectionModel().selectFirst();
        departmentComboBox.getSelectionModel().selectFirst();
        sexComboBox.getSelectionModel().clearSelection();

        // Uncheck show password
        showPasswordCheckbox.setSelected(false);

        // Reset styles to green border
        applyGreenBorder(emailField);
        applyGreenBorder(phoneField);
        applyGreenBorder(employeeIdField);
        applyGreenBorder(fullNameField);
        applyGreenBorder(usernameField);
        applyGreenBorder(passwordField);
        applyGreenBorder(confirmPasswordField);

        errorLabel.setVisible(false);
    }

    private void validateForm() {
        boolean isEmailValid = EMAIL_PATTERN.matcher(emailField.getText().trim()).matches();
        boolean isEmployeeIdValid = !employeeIdField.getText().trim().isEmpty();
        createAccountButton.setDisable(!isEmailValid || !isEmployeeIdValid);
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            // Use the new switchScene method with maximized=false for login
            Main.switchScene("/view/login.fxml", false);
            stage.setTitle("Instant Notification System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: Show error and close current window
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Cannot navigate to login");
            alert.setContentText("Please close this window and open the login screen manually.");
            alert.showAndWait();

            // Close current window
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            stage.close();
        }
    }
}
