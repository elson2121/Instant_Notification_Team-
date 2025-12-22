package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button btnLogout;

    @FXML
    private ListView<String> notificationsList;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Welcome, User!");

        // Setup logout button
        btnLogout.setOnAction(e -> SceneSwitcher.switchToLogin());

        // Load user notifications
        loadNotifications();
    }

    private void loadNotifications() {
        // TODO: Load actual notifications from database
        notificationsList.getItems().addAll(
                "📢 System Maintenance - Tonight 2 AM",
                "🎉 Company Meeting - Tomorrow 10 AM",
                "⚠️ Security Alert - Update Your Password",
                "📅 Performance Review - Next Week",
                "🏆 Employee of the Month Announcement"
        );
    }
}