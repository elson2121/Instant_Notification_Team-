// RegisterController.java
package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private TextField employeeIdField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private ComboBox<String> shiftComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private Label errorLabel;

    private AuthService authService;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @FXML
    public void initialize() {
        authService = new AuthService();
        setupComboBoxes();
    }

    private void setupComboBoxes() {
        roleComboBox.getItems().addAll("USER", "ADMIN");
        sexComboBox.getItems().addAll("Male", "Female", "Other");
        shiftComboBox.getItems().addAll("Day", "Night", "Flexible");
        departmentComboBox.getItems().addAll(
                "Human Resources", "Information Technology", "Finance",
                "Operations", "Marketing", "Sales", "Research & Development"
        );

        // Set defaults
        roleComboBox.setValue("USER");
        sexComboBox.setValue("Male");
        shiftComboBox.setValue("Day");
        departmentComboBox.setValue("Information Technology");
    }

    @FXML
    private void handleRegister() {
        if (validateInput()) {
            User user = createUserFromInput();

            if (authService.registerUser(user)) {
                showSuccess("Registration successful! Please login.");
                clearForm();
            } else {
                showError("Registration failed. Username may already exist.");
            }
        }
    }

    private boolean validateInput() {
        // Check mandatory fields
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Full name is required");
            return false;
        }

        if (!USERNAME_PATTERN.matcher(usernameField.getText()).matches()) {
            showError("Username must be 3-20 characters (letters, numbers, underscore)");
            return false;
        }

        if (passwordField.getText().length() < 6) {
            showError("Password must be at least 6 characters");
            return false;
        }

        // Phone validation (mandatory)
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            showError("Phone number is mandatory");
            return false;
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            showError("Invalid phone number format (10-15 digits, + allowed)");
            return false;
        }

        return true;
    }

    private User createUserFromInput() {
        User user = new User();
        user.setFullName(fullNameField.getText().trim());
        user.setUsername(usernameField.getText().trim());
        user.setPassword(passwordField.getText());
        user.setPhoneNumber(phoneField.getText().trim());
        user.setEmployeeId(employeeIdField.getText().trim().isEmpty() ? null : employeeIdField.getText().trim());
        user.setRole(roleComboBox.getValue());
        user.setSex(sexComboBox.getValue());
        user.setShift(shiftComboBox.getValue());
        user.setDepartmentName(departmentComboBox.getValue());

        return user;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().remove("success-label");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().remove("error-label");
        errorLabel.getStyleClass().add("success-label");
        errorLabel.setVisible(true);
    }

    private void clearForm() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        phoneField.clear();
        employeeIdField.clear();
        roleComboBox.setValue("USER");
        sexComboBox.setValue("Male");
        shiftComboBox.setValue("Day");
        departmentComboBox.setValue("Information Technology");
    }
}