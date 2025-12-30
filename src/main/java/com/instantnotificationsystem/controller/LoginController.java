package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.Main;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.service.SessionManager;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginPasswordFieldVisible;
    @FXML private CheckBox showLoginPasswordCheckbox;
    @FXML private Label loginErrorLabel;

    private UserDAO userDAO;

    public void initialize() {
        userDAO = new UserDAO();
        setupPasswordVisibility();
    }

    private void setupPasswordVisibility() {
        showLoginPasswordCheckbox.selectedProperty().addListener((obs, was, isNow) -> {
            loginPasswordFieldVisible.setText(loginPasswordField.getText());
            loginPasswordFieldVisible.setVisible(isNow);
            loginPasswordField.setVisible(!isNow);
        });
        loginPasswordField.textProperty().bindBidirectional(loginPasswordFieldVisible.textProperty());
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.isVisible() ? loginPasswordField.getText() : loginPasswordFieldVisible.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }

        // Hardcoded Admin Login - Updated password to "admin1"
        if ("admin".equals(username) && "admin1".equals(password)) {
            User adminUser = new User();
            adminUser.setId(1); // Assuming Admin ID is 1
            adminUser.setFullName("Administrator");
            adminUser.setUsername("admin");
            adminUser.setRole("Admin");
            SessionManager.setUser(adminUser);
            loadDashboard(adminUser);
            return;
        }

        User user = userDAO.getUserByUsernameAndPassword(username, password);

        if (user != null) {
            if (!user.isActive()) {
                showError("Your account has been blocked. Please contact the administrator.");
                return;
            }
            SessionManager.setUser(user);
            loadDashboard(user);
        } else {
            showError("Invalid username or password");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            // Use the new switchScene method with maximized=false for registration
            Main.switchScene("/view/register.fxml", false);
            stage.setTitle("Instant Notification System - Register");
        } catch (Exception e) {
            showError("Could not load registration screen.");
            e.printStackTrace();
        }
    }

    private void loadDashboard(User user) {
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                SceneSwitcher.switchToAdminDashboard(stage, user.getFullName(), user.getId());
            } else {
                SceneSwitcher.switchToDashboard(stage, user.getFullName(), user.getId());
            }
        } catch (IOException ex) {
            showError("Error loading dashboard: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        loginErrorLabel.setText(message);
        loginErrorLabel.setStyle("-fx-text-fill: #ef4444;");
        loginErrorLabel.setVisible(true);
    }
}
