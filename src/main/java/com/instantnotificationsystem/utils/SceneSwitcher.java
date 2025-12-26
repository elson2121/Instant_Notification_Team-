package com.instantnotificationsystem.utils;

import com.instantnotificationsystem.controller.UserDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    /**
     * Switch to any scene with FXML path
     */
    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Special method to switch to user dashboard with user data
     */
    public static void switchToDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/view/user_dashboard.fxml"));
        Parent root = loader.load();

        // Get controller and set user data
        UserDashboardController controller = loader.getController();
        controller.setUserName(fullName);  // Pass actual full name
        controller.setUserId(userId);      // Pass user ID

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Special method to switch to admin dashboard with user data
     */
    public static void switchToAdminDashboard(Stage stage, String fullName, int userId) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource("/view/admin_dashboard.fxml"));
        Parent root = loader.load();

        // Assuming AdminDashboardController exists and has similar methods
        // If not, we might need to adjust this part or create the controller
        // For now, we'll just load the scene. If you have an AdminDashboardController,
        // you should cast it here and set the user data like above.
        
        // Example (uncomment if AdminDashboardController exists):
        // AdminDashboardController controller = loader.getController();
        // controller.setUserName(fullName);
        // controller.setUserId(userId);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}