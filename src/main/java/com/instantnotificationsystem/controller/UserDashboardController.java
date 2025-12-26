package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserDashboardController {

    @FXML
    private ListView<Notification> notificationsListView;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Button btnLogout;
    @FXML
    private Label notificationBadge;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnNotifications;
    @FXML
    private Button btnMyNotifications;

    private NotificationDAO notificationDAO;
    private int currentUserId;
    private String currentUserName;
    private ScheduledExecutorService scheduler;

    public void initialize() {
        notificationDAO = new NotificationDAO();
        updateWelcomeMessage();
        updateDateDisplay();
        setupNotificationList();
        setActiveButton(btnDashboard);
        startRealTimeUpdates();
    }

    private void startRealTimeUpdates() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (currentUserId > 0) {
                    loadNotifications();
                    updateNotificationBadge();
                }
            });
        }, 0, 5, TimeUnit.SECONDS); // Refresh every 5 seconds
    }

    public void setUserName(String userName) {
        this.currentUserName = userName;
        updateWelcomeMessage();
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadNotifications();
        updateNotificationBadge();
    }

    private void loadNotifications() {
        if (currentUserId > 0) {
            List<Notification> notifications = notificationDAO.getNotificationsForUser(currentUserId);
            notificationsListView.setItems(FXCollections.observableArrayList(notifications));
        }
    }

    private void updateWelcomeMessage() {
        if (welcomeLabel != null) {
            String displayName = (currentUserName != null && !currentUserName.trim().isEmpty()) ? currentUserName : "User";
            welcomeLabel.setText("Welcome, " + displayName + "!");
        }
    }

    private void updateDateDisplay() {
        if (dateLabel != null) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy");
            dateLabel.setText(today.format(dateFormatter));
        }
    }

    private void setupNotificationList() {
        notificationsListView.setCellFactory(param -> new ListCell<Notification>() {
            private final VBox contentBox = new VBox(10);
            private final Label titleLabel = new Label();
            private final Text messageText = new Text();
            private final HBox channelBox = new HBox(8);

            {
                messageText.setWrappingWidth(500);
                contentBox.setPadding(new Insets(15));
                contentBox.getChildren().addAll(titleLabel, messageText, channelBox);
            }

            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    titleLabel.setText(notification.getTitle());
                    messageText.setText(notification.getMessage());

                    channelBox.getChildren().clear();
                    for (String channel : notification.getChannels()) {
                        Label channelLabel = new Label(channel);
                        channelLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #5A88FF; -fx-background-color: #eff6ff; -fx-padding: 3 8; -fx-background-radius: 6;");
                        channelBox.getChildren().add(channelLabel);
                    }

                    // Use 'seen' status from the database to set font weight
                    if (!notification.isSeen()) {
                        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 17px; -fx-text-fill: #2D62ED;");
                        messageText.setStyle("-fx-fill: #374151;");
                    } else {
                        titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 17px; -fx-text-fill: #6b7280;");
                        messageText.setStyle("-fx-fill: #9ca3af;");
                    }
                    setGraphic(contentBox);
                }
            }
        });

        notificationsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null && !val.isSeen()) {
                markAsSeen(val.getId());
                val.setSeen(true);
                notificationsListView.refresh();
                updateNotificationBadge();
            }
        });
    }

    @FXML
    private void handleLogout() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            SceneSwitcher.switchScene(stage, "/view/login.fxml");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void markAsSeen(int notificationId) {
        if (currentUserId > 0) {
            new Thread(() -> notificationDAO.markNotificationAsSeen(currentUserId, notificationId)).start();
        }
    }

    private void updateNotificationBadge() {
        if (notificationBadge != null && currentUserId > 0) {
            int unreadCount = notificationDAO.getUnreadNotificationCount(currentUserId);
            notificationBadge.setVisible(unreadCount > 0);
            notificationBadge.setText(String.valueOf(unreadCount));
        }
    }

    @FXML
    private void handleBellClick() {
        if (currentUserId > 0) {
            if (notificationDAO.markAllNotificationsAsSeen(currentUserId)) {
                notificationsListView.getItems().forEach(n -> n.setSeen(true));
                notificationsListView.refresh();
                updateNotificationBadge();
            }
        }
    }

    @FXML
    private void handleDashboard() {
        setActiveButton(btnDashboard);
    }

    @FXML
    private void handleNotifications() {
        setActiveButton(btnNotifications);
    }

    @FXML
    private void handleMyNotifications() {
        setActiveButton(btnMyNotifications);
    }

    private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("sidebar-button-active");
        btnNotifications.getStyleClass().remove("sidebar-button-active");
        btnMyNotifications.getStyleClass().remove("sidebar-button-active");

        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
    }
}