package com.instantnotificationsystem;

import com.instantnotificationsystem.config.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize Database
        DBConnection.initializeTables();

        primaryStage = stage;

        // Load login screen
        loadFXML("/view/login.fxml", "Instant Notification System - Login");

        // Set stage properties
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    // Static method to switch scenes from any controller
    public static void loadFXML(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/resources/style.css").toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
        } catch (Exception e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}