// UserDashboardController.java (Partial - seen status logic)
package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.model.Notification;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class UserDashboardController {

    @FXML
    private ListView<Notification> notificationsListView;

    private NotificationDAO notificationDAO;
    private int currentUserId;

    public void initialize() {
        notificationDAO = new NotificationDAO();

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