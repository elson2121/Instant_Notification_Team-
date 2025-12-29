package com.instantnotificationsystem;

import com.instantnotificationsystem.config.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static Stage primaryStage;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) {
        // Initialize Database
        DBConnection.initializeTables();

        primaryStage = stage;

        // Load login screen
        switchScene("/view/login.fxml", "Instant Notification System - Login", false);

        primaryStage.show();
    }

    // Static method to switch scenes from any controller
    public static void switchScene(String fxmlPath, String title, boolean maximized) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            URL css = Main.class.getResource("/style.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            } else {
                LOGGER.log(Level.WARNING, "Could not find stylesheet /style.css");
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);

            // Reset minimum dimensions to allow smaller screens (like Login)
            primaryStage.setMinWidth(0);
            primaryStage.setMinHeight(0);

            // Stage Refresh: Reset boundaries
            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();

            if (maximized) {
                primaryStage.setMaximized(true);
            } else {
                primaryStage.setMaximized(false);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading FXML: " + fxmlPath, e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}