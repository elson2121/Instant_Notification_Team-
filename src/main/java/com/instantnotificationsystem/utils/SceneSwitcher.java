package com.instantnotificationsystem.utils;

import com.instantnotificationsystem.controller.AdminDashboardController;
import com.instantnotificationsystem.controller.UserDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    public static void switchToDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/view/user_dashboard.fxml"));
        Parent root = loader.load();
        UserDashboardController controller = loader.getController();
        controller.initData(fullName, userId);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(SceneSwitcher.class.getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("User Dashboard");
        stage.show();
    }

    public static void switchToAdminDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/view/admin_dashboard.fxml"));
        Parent root = loader.load();
        AdminDashboardController controller = loader.getController();
        controller.initData(fullName, userId);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(SceneSwitcher.class.getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard");
        stage.show();
    }
}