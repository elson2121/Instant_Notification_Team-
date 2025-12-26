package com.instantnotificationsystem.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneSwitcher {

    public static void switchScene(Stage stage, String fxmlFile) throws IOException {
        URL fxmlLocation = SceneSwitcher.class.getResource(fxmlFile);
        if (fxmlLocation == null) {
            throw new IOException("Cannot find FXML file: " + fxmlFile);
        }
        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}