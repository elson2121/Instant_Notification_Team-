package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginErrorLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter username and password");
            return;
        }

        // Hardcoded Admin Check
        if ("admin".equals(username) && "admin123".equals(password)) {
            try {
                Stage stage = (Stage) loginUsernameField.getScene().getWindow();
                SceneSwitcher.switchScene(stage, "/view/admin_dashboard.fxml");
                stage.setTitle("Dashboard");
            } catch (IOException e) {
                showLoginError("Failed to load Admin Dashboard: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        // Regular User Check via Database
        User user = userDAO.getUserByUsernameAndPassword(username, password);
        if (user != null) {
            try {
                Stage stage = (Stage) loginUsernameField.getScene().getWindow();
                SceneSwitcher.switchScene(stage, "/view/user_dashboard.fxml");
                stage.setTitle("Dashboard");
            } catch (IOException e) {
                showLoginError("Failed to load User Dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showLoginError("Invalid username or password");
        }
    }

    private void showLoginError(String message) {
        if (loginErrorLabel != null) {
            loginErrorLabel.setText(message);
            loginErrorLabel.setVisible(true);
        } else {
            System.err.println("Login Error: " + message);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/view/register.fxml");
            stage.setTitle("Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showLoginView() {
        // Logic to show login view if needed
    }

    @FXML
    private void handleNextStep() {
        // Logic for next step in registration
    }

    @FXML
    private void handlePrevStep() {
        // Logic for previous step in registration
    }

    @FXML
    protected void onHelloButtonClick() {
        // Kept for compatibility if FXML still references it
    }
}