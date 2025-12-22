// Location: src/main/java/com/instantnotificationsystem/controller/LoginController.java
package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        // Initialize role options
        roleComboBox.getItems().addAll("USER", "ADMIN");
        roleComboBox.setValue("USER");

        // Clear error label
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        // Clear previous error
        errorLabel.setVisible(false);

        // Simple login logic (you'll replace this with database auth later)
        if ("admin".equalsIgnoreCase(username) && "admin".equals(password)) {
            // Switch to admin dashboard
            SceneSwitcher.switchToAdminDashboard();
        } else if ("user".equalsIgnoreCase(username) && "user".equals(password)) {
            // Switch to user dashboard
            SceneSwitcher.switchToUserDashboard();
        } else {
            // Test users for demonstration
            if (username.contains("admin")) {
                SceneSwitcher.switchToAdminDashboard();
            } else {
                SceneSwitcher.switchToUserDashboard();
            }
        }
    }

    @FXML
    private void handleRegister(MouseEvent event) {
        // Switch to registration screen
        SceneSwitcher.switchToRegister();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}