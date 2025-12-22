// Main.java
package com.instantnotificationsystem;

import com.instantnotificationsystem.config.DBConnection;
import com.instantnotificationsystem.service.SchedulerService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {

    private static SchedulerService schedulerService;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database connection
            initializeDatabase();

            // Initialize scheduler service
            schedulerService = new SchedulerService();
            schedulerService.start();

            // Load login screen
            URL fxmlLocation = getClass().getResource("/view/login.fxml");
            if (fxmlLocation == null) {
                throw new IllegalStateException("Cannot find /view/login.fxml");
            }
            Parent root = FXMLLoader.load(fxmlLocation);
            Scene scene = new Scene(root, 1000, 700);

            // Apply CSS
            URL cssLocation = getClass().getResource("/resources/style.css");
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            } else {
                System.err.println("Warning: /resources/style.css not found");
            }

            primaryStage.setTitle("Instant Notification System v1.0");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            primaryStage.show();

            // Handle window close event
            primaryStage.setOnCloseRequest(event -> {
                shutdown();
                Platform.exit();
                System.exit(0);
            });

        } catch (Exception e) {
            showErrorDialog("Application Startup Error",
                    "Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeDatabase() {
        try {
            if (DBConnection.testConnection()) {
                System.out.println("✓ Database connection successful!");

                // Check and create tables if they don't exist
                DBConnection.initializeTables();

            } else {
                throw new Exception("Cannot connect to MySQL database.");
            }
        } catch (Exception e) {
            showErrorDialog("Database Connection Failed",
                    "Please ensure:\n" +
                            "1. MySQL Server is running\n" +
                            "2. Database 'instant_notification_system' exists\n" +
                            "3. Correct credentials in DBConnection.java\n\n" +
                            "Error: " + e.getMessage());
            Platform.exit();
            System.exit(1);
        }
    }

    private void shutdown() {
        if (schedulerService != null) {
            schedulerService.shutdown();
        }
        DBConnection.closeConnection();
        System.out.println("Application shutdown complete.");
    }

    private void showErrorDialog(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}