package com.instantnotificationsystem.utils;

import com.instantnotificationsystem.Main;
import com.instantnotificationsystem.controller.AdminDashboardController;
import com.instantnotificationsystem.controller.UserDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchToDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = Main.switchScene("/view/user_dashboard.fxml", true);
        if (loader != null) {
            UserDashboardController controller = loader.getController();
            controller.initData(fullName, userId);
            stage.setTitle("User Dashboard");
        }
    }

    public static void switchToAdminDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = Main.switchScene("/view/admin_dashboard.fxml", true);
        if (loader != null) {
            AdminDashboardController controller = loader.getController();
            controller.initData(fullName, userId);
            stage.setTitle("Admin Dashboard");
        }
    }
}
