package com.instantnotificationsystem;

import com.instantnotificationsystem.config.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static Stage primaryStage;
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) {
        // Initialize Database
        DBConnection.setupDatabase();

        primaryStage = stage;

        // Unlock Window Bounds
        primaryStage.setResizable(true);

        // Load login screen
        switchScene("/view/login.fxml", "Instant Notification System - Login");

        primaryStage.show();
    }

    // Static method to switch scenes from any controller
    public static FXMLLoader switchScene(String fxmlPath, boolean maximized) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Responsive Roots: Ensure root uses computed size
            if (root instanceof Region) {
                ((Region) root).setPrefWidth(Region.USE_COMPUTED_SIZE);
                ((Region) root).setPrefHeight(Region.USE_COMPUTED_SIZE);
            }

            // Create scene if it doesn't exist or replace root
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            // Apply CSS
            URL css = Main.class.getResource("/style.css");
            if (css != null) {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(css.toExternalForm());
            } else {
                LOGGER.log(Level.WARNING, "Could not find stylesheet /style.css");
            }

            // Dynamic Sizing Logic
            if (maximized) {
                primaryStage.setMaximized(true);
            } else {
                primaryStage.setMaximized(false);
                primaryStage.sizeToScene();
                primaryStage.centerOnScreen();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading FXML: " + fxmlPath, e);
        }
        return loader;
    }

    // Overload for backward compatibility (defaults to non-maximized)
    public static FXMLLoader switchScene(String fxmlPath, String title) {
        primaryStage.setTitle(title);
        return switchScene(fxmlPath, false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}