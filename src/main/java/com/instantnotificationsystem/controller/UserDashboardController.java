package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private VBox notificationContainer;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, User!");
        
        // Load default tab content
        loadNotifications("Fisht");
    }

    @FXML
    private void handleTabSwitch(ActionEvent event) {
        Button clickedBtn = (Button) event.getSource();
        String category = clickedBtn.getText();
        loadNotifications(category);
    }

    @FXML
    private void handleLogout() {
        SceneSwitcher.switchToLogin();
    }

    private void loadNotifications(String category) {
        if (notificationContainer == null) return;
        
        notificationContainer.getChildren().clear();
        
        // Mock data based on category
        // In a real app, this would query the DB based on the category/filter
        for (int i = 1; i <= 6; i++) {
            notificationContainer.getChildren().add(createNotificationCard(category + " Update " + i));
        }
    }

    private HBox createNotificationCard(String title) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 5);");
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Icon/Image placeholder
        StackPane iconPane = new StackPane();
        Circle bg = new Circle(25, Color.web("#e3f2fd"));
        Label icon = new Label("🔔"); // Changed back to Bell
        icon.setStyle("-fx-font-size: 20px;");
        iconPane.getChildren().addAll(bg, icon);
        
        // Content
        VBox content = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        
        Label descLabel = new Label("This is a sample notification description for " + title + ". It contains important information.");
        descLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-wrap-text: true;");
        
        content.getChildren().addAll(titleLabel, descLabel);
        HBox.setHgrow(content, Priority.ALWAYS);
        
        // Action Button
        Button actionBtn = new Button("Amroll Here");
        actionBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand; -fx-font-weight: bold;");
        
        card.getChildren().addAll(iconPane, content, actionBtn);
        
        return card;
    }
}