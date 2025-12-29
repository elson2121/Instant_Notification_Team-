package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.Main;
import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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
    private Button btnHistory;

    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    private int currentUserId;
    private String currentUserName;
    private ScheduledExecutorService scheduler;
    private boolean showingHistory = false;

    public void initialize() {
        notificationDAO = new NotificationDAO();
        userDAO = new UserDAO();
        updateWelcomeMessage();
        updateDateDisplay();
        setupNotificationList();
        setActiveButton(btnNotifications);
        loadInitialNotifications();
        startRealTimeUpdates();
    }

    public void initData(String fullName, int userId) {
        this.currentUserName = fullName;
        this.currentUserId = userId;
        updateWelcomeMessage();
        loadInitialNotifications();
        updateNotificationBadge();
    }

    private void startRealTimeUpdates() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (currentUserId > 0) {
                    checkUserStatus();
                    loadNotifications();
                    updateNotificationBadge();
                }
            });
        }, 0, 5, TimeUnit.SECONDS); // Refresh every 5 seconds
    }

    private void checkUserStatus() {
        User user = userDAO.getUserById(currentUserId);
        if (user != null && !user.isActive()) {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Account Blocked");
            alert.setContentText("Your account has been blocked. Please contact the administrator.");
            alert.showAndWait();
            handleLogout();
        }
    }

    public void setUserName(String userName) {
        this.currentUserName = userName;
        updateWelcomeMessage();
    }

    public void setUserId(int userId) {
        this.currentUserId = userId;
        loadInitialNotifications();
        updateNotificationBadge();
    }

    private void loadInitialNotifications() {
        showingHistory = false;
        loadNotifications();
    }

    private void loadNotifications() {
        if (currentUserId > 0) {
            int index = notificationsListView.getSelectionModel().getSelectedIndex();
            List<Notification> notifications;
            if (showingHistory) {
                notifications = notificationDAO.getReadNotificationsForUser(currentUserId);
            } else {
                notifications = notificationDAO.getUnreadNotificationsForUser(currentUserId);
            }
            notificationsListView.setItems(FXCollections.observableArrayList(notifications));
            if (notifications.isEmpty()) {
                Label placeholder = new Label("You are all caught up!");
                placeholder.setStyle("-fx-text-fill: #9ca3af;");
                notificationsListView.setPlaceholder(placeholder);
            }
            notificationsListView.getSelectionModel().select(index);
        }
    }
    
    private void loadHistory() {
        if (currentUserId > 0) {
            int index = notificationsListView.getSelectionModel().getSelectedIndex();
            List<Notification> notifications = notificationDAO.getReadNotificationsForUser(currentUserId);
            notificationsListView.setItems(FXCollections.observableArrayList(notifications));
            if (notifications.isEmpty()) {
                Label placeholder = new Label("Your history is empty");
                placeholder.setStyle("-fx-text-fill: #9ca3af;");
                notificationsListView.setPlaceholder(placeholder);
            }
            notificationsListView.getSelectionModel().select(index);
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
            private final Button deleteButton = new Button("Delete");
            private final Region spacer = new Region();
            private final HBox mainContent = new HBox(10, contentBox, spacer, deleteButton);

            {
                messageText.setWrappingWidth(500);
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                mainContent.setPadding(new Insets(15));
                contentBox.getChildren().addAll(titleLabel, messageText, channelBox);
                setGraphic(mainContent);
            }

            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                if (empty || notification == null) {
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                    setPadding(new Insets(0, 0, 0, 0));
                } else {
                    titleLabel.setText(notification.getTitle());
                    messageText.setText(notification.getMessage());

                    channelBox.getChildren().clear();
                    for (String channel : notification.getChannels()) {
                        Label channelLabel = new Label(channel);
                        channelLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #5A88FF; -fx-background-color: #eff6ff; -fx-padding: 3 8; -fx-background-radius: 6;");
                        channelBox.getChildren().add(channelLabel);
                    }

                    if (!notification.isSeen()) {
                        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 17px; -fx-text-fill: #2D62ED;");
                        messageText.setStyle("-fx-fill: #374151;");
                        deleteButton.setVisible(false);
                    } else {
                        titleLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 17px; -fx-text-fill: #6b7280;");
                        messageText.setStyle("-fx-fill: #9ca3af;");
                        deleteButton.setVisible(true);
                    }
                    
                    deleteButton.setOnAction(event -> {
                        if (notificationDAO.deleteNotificationForUser(currentUserId, notification.getId())) {
                            if (showingHistory) {
                                loadHistory();
                            } else {
                                loadNotifications();
                            }
                        }
                    });

                    setGraphic(mainContent);
                    setPadding(new Insets(0, 0, 10, 0));
                }
            }
        });

        notificationsListView.setOnMouseClicked(event -> {
            Notification selectedNotification = notificationsListView.getSelectionModel().getSelectedItem();
            if (selectedNotification != null && !selectedNotification.isSeen()) {
                markAsSeen(selectedNotification.getId());
                selectedNotification.setSeen(true);
                
                // Remove the item from the list and refresh
                notificationsListView.getItems().remove(selectedNotification);
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
        Main.switchScene("/view/login.fxml", "Instant Notification System - Login", false);
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
        // You might want to define what happens here, e.g., load a default view
    }

    @FXML
    private void handleNotifications() {
        showingHistory = false;
        setActiveButton(btnNotifications);
        loadNotifications();
    }

    @FXML
    private void handleHistory() {
        showingHistory = true;
        setActiveButton(btnHistory);
        loadHistory();
    }

    private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("sidebar-button-active");
        btnNotifications.getStyleClass().remove("sidebar-button-active");
        btnHistory.getStyleClass().remove("sidebar-button-active");

        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar-button-active");
        }
    }
}