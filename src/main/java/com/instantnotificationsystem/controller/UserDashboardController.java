package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;

public class UserDashboardController {

    @FXML
    private ListView<Notification> notificationsListView;
    
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button btnLogout;

    private NotificationDAO notificationDAO;
    private int currentUserId;

    public void initialize() {
        notificationDAO = new NotificationDAO();
        
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, User!");
        }

        if (notificationsListView != null) {
            // Custom cell factory for automatic seen status
            notificationsListView.setCellFactory(param -> new ListCell<Notification>() {
                @Override
                protected void updateItem(Notification notification, boolean empty) {
                    super.updateItem(notification, empty);

                    if (empty || notification == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Create custom notification display
                        setText(notification.getTitle());

                        // Mark as seen when it appears in list
                        if (!notification.isSeen()) {
                            markAsSeen(notification.getId());
                            notification.setSeen(true);
                        }
                    }
                }
            });

            // Handle notification selection
            notificationsListView.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            showNotificationDetails(newValue);
                            // Ensure it's marked as seen when opened
                            markAsSeen(newValue.getId());
                        }
                    });
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/view/login.fxml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void markAsSeen(int notificationId) {
        new Thread(() -> {
            boolean success = notificationDAO.markNotificationAsSeen(currentUserId, notificationId);
            if (success) {
                System.out.println("✓ Notification " + notificationId + " marked as seen");
            }
        }).start();
    }

    private void showNotificationDetails(Notification notification) {
        // Show detailed view and automatically mark as seen
        markAsSeen(notification.getId());

        // Update UI to show details
        // ...
    }
}