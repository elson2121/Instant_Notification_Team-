package com.instantnotificationsystem.utils;

import com.instantnotificationsystem.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSwitcher {

    // Switch to any FXML without closing window
    public static void switchTo(String fxmlPath, String title) {
        try {
            Main.loadFXML(fxmlPath, title);
        } catch (Exception e) {
            System.err.println("Scene switch error: " + e.getMessage());
        }
    }

    // Switch to login
    public static void switchToLogin() {
        switchTo("/view/login.fxml", "Instant Notification System - Login");
    }

    // Switch to admin dashboard
    public static void switchToAdminDashboard() {
        switchTo("/view/admin_dashboard.fxml", "Admin Dashboard");
    }

    // Switch to user dashboard
    public static void switchToUserDashboard() {
        switchTo("/view/user_dashboard.fxml", "User Dashboard");
    }

    // Switch to registration
    public static void switchToRegister() {
        switchTo("/view/register.fxml", "User Registration");
    }
}