package com.instantnotificationsystem.utils;

import com.instantnotificationsystem.controller.AdminDashboardController;
import com.instantnotificationsystem.controller.UserDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void switchToDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/view/user_dashboard.fxml"));
        Parent root = loader.load();

        UserDashboardController controller = loader.getController();
        controller.setUserName(fullName);
        controller.setUserId(userId);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void switchToAdminDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/view/admin_dashboard.fxml"));
        Parent root = loader.load();

        AdminDashboardController controller = loader.getController();
        // controller.setUserName(fullName); // Admin name is now set in the controller
        // controller.setUserId(userId); // User ID is now managed by SessionManager

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}