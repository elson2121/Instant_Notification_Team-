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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserDashboardController {

    @FXML
    private ListView<Notification> notificationsListView;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label yearLabel;

    @FXML
    private Button btnLogout;

    private NotificationDAO notificationDAO;
    private int currentUserId;
    private String currentUserName;

    public void initialize() {
        notificationDAO = new NotificationDAO();

        // Set welcome message with actual user's name
        updateWelcomeMessage();

        // Set current date
        updateDateDisplay();

        // Setup notification list
        if (notificationsListView != null) {
            setupNotificationList();
        }
    }

    /**
     * Sets the logged-in user's actual full name (called from login controller)
     * This is the full_name from registration stored in database
     */
    public void setUserName(String userName) {
        this.currentUserName = userName;
        updateWelcomeMessage();
    }

    /**
     * Sets the current user ID for notification loading
     */
    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadUserNotifications();
    }

    /**
     * Updates welcome label with actual user's full name from registration
     */
    private void updateWelcomeMessage() {
        if (welcomeLabel != null) {
            String displayName = (currentUserName != null && !currentUserName.trim().isEmpty())
                    ? currentUserName
                    : "User";
            welcomeLabel.setText("Welcome, " + displayName + "!");
        }
    }

    /**
     * Updates date display with current date
     */
    private void updateDateDisplay() {
        if (dateLabel != null && yearLabel != null) {
            LocalDate today = LocalDate.now();

            // Format full date
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy");
            dateLabel.setText(today.format(dateFormatter));

            // Set year separately
            yearLabel.setText(String.valueOf(today.getYear()));
        }
    }

    /**
     * Sets up notification list with custom styling
     */
    private void setupNotificationList() {
        // Custom cell factory for notifications
        notificationsListView.setCellFactory(param -> new ListCell<Notification>() {
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);

                if (empty || notification == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    // Show title in the list
                    setText(notification.getTitle());

                    // Style based on seen status
                    if (!notification.isSeen()) {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #2D62ED;");
                        // Auto-mark as seen when displayed
                        if (currentUserId > 0) {
                            markAsSeen(notification.getId());
                            notification.setSeen(true);
                        }
                    } else {
                        setStyle("-fx-text-fill: #374151;");
                    }
                }
            }
        });

        // Handle notification selection
        notificationsListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        showNotificationDetails(newValue);
                    }
                });
    }

    /**
     * Logout button handler - closes dashboard and returns to login
     */
    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/view/login.fxml");
        } catch (IOException ex) {
            System.err.println("Error during logout: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Loads notifications for the current user
     */
    private void loadUserNotifications() {
        if (currentUserId > 0) {
            System.out.println("Loading notifications for user ID: " + currentUserId);
            // TODO: Implement actual notification loading
            // Example:
            // List<Notification> notifications = notificationDAO.getNotificationsForUser(currentUserId);
            // notificationsListView.getItems().setAll(notifications);
        }
    }

    /**
     * Marks a notification as seen (thread-safe)
     */
    private void markAsSeen(int notificationId) {
        if (currentUserId > 0) {
            new Thread(() -> {
                boolean success = notificationDAO.markNotificationAsSeen(currentUserId, notificationId);
                if (success) {
                    System.out.println("✓ Notification " + notificationId + " marked as seen");
                }
            }).start();
        }
    }

    /**
     * Shows detailed view of selected notification
     * Using getMessage() from Notification model
     */
    private void showNotificationDetails(Notification notification) {
        // Mark as seen
        markAsSeen(notification.getId());

        // Update UI
        notification.setSeen(true);
        notificationsListView.refresh();

        // Show notification details
        System.out.println("=== Notification Details ===");
        System.out.println("Title: " + notification.getTitle());
        System.out.println("Message: " + notification.getMessage());
        System.out.println("Scheduled Time: " +
                (notification.getScheduledTime() != null ?
                        notification.getScheduledTime().toString() : "Immediate"));
        System.out.println("Status: " + (notification.isSeen() ? "Read" : "Unread"));
        System.out.println("========================");
    }
}