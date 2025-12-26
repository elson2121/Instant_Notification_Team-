package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordFieldVisible;
    @FXML private CheckBox showPasswordCheckbox;
    @FXML private TextField phoneField;
    @FXML private TextField employeeIdField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private ComboBox<String> shiftComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private Label errorLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        setupComboBoxes();
        setupPasswordVisibilityToggle();
    }

    private void setupPasswordVisibilityToggle() {
        // Bind the visibility of the two fields
        passwordFieldVisible.managedProperty().bind(showPasswordCheckbox.selectedProperty());
        passwordFieldVisible.visibleProperty().bind(showPasswordCheckbox.selectedProperty());
        passwordField.managedProperty().bind(showPasswordCheckbox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckbox.selectedProperty().not());

        // Bind the text content of the two fields together
        passwordFieldVisible.textProperty().bindBidirectional(passwordField.textProperty());
    }

    private void setupComboBoxes() {
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("USER", "ADMIN");
            roleComboBox.setValue("USER");
        }
        if (sexComboBox != null) {
            sexComboBox.getItems().addAll("Male", "Female", "Other");
            sexComboBox.setValue("Male");
        }
        if (shiftComboBox != null) {
            shiftComboBox.getItems().addAll("Day", "Night", "Flexible");
            shiftComboBox.setValue("Day");
        }
        if (departmentComboBox != null) {
            departmentComboBox.getItems().addAll(
                    "Human Resources", "Information Technology", "Finance",
                    "Operations", "Marketing", "Sales", "Research & Development"
            );
            departmentComboBox.setValue("Information Technology");
        }
    }

    @FXML
    private void handleRegister() {
        // 1. Get text from fields
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String department = departmentComboBox.getValue();
        
        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and Password are required.");
            return;
        }

        // 2. Create User object
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setDepartmentName(department);
        
        // Set other fields if available, handling potential nulls
        if (fullNameField != null) user.setFullName(fullNameField.getText().trim());
        if (phoneField != null) user.setPhoneNumber(phoneField.getText().trim());
        if (employeeIdField != null) user.setEmployeeId(employeeIdField.getText().trim());
        if (roleComboBox != null) user.setRole(roleComboBox.getValue());
        if (sexComboBox != null) user.setSex(sexComboBox.getValue());
        if (shiftComboBox != null) user.setShift(shiftComboBox.getValue());

        // 3. Save to database
        if (userDAO.createUser(user)) {
            // 4. Show success and redirect
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("User registered successfully! Redirecting to login...");
            alert.showAndWait();

            handleBackToLogin();
        } else {
            showError("Registration failed. Username might already exist.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/view/login.fxml");
            stage.setTitle("Instant Notification System - Login");
        } catch (Exception e) {
            showError("Failed to redirect to login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setStyle("-fx-text-fill: red; -fx-background-color: #fee2e2; -fx-padding: 10; -fx-background-radius: 6;");
        } else {
            // Fallback if label is missing
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.show();
        }
    }
}