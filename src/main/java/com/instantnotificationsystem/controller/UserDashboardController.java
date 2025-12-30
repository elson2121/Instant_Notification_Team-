package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.Main;
import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.service.SessionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserDashboardController {

    @FXML private ListView<Notification> notificationsListView;
    @FXML private Label welcomeLabel;
    @FXML private Button btnLogout;
    @FXML private Label notificationBadge;
    @FXML private StackPane notificationBadgeContainer;
    @FXML private Label listTitle;

    @FXML private VBox dashboardCard;
    @FXML private StackPane notificationsCard;
    @FXML private VBox historyCard;

    // User Info Labels
    @FXML private Label lblDepartment;
    @FXML private Label lblRole;
    @FXML private Label lblSex;
    @FXML private Label lblShift;

    @FXML private ScrollPane profileView;
    @FXML private VBox notificationsView;

    private NotificationDAO notificationDAO;
    private UserDAO userDAO;
    private int currentUserId;
    private String currentUserName;
    private ScheduledExecutorService scheduler;
    
    // Track notifications seen in the current session to keep them in the list until refresh
    private List<Notification> sessionSeenNotifications = new ArrayList<>();
    
    private enum ViewMode {
        DASHBOARD, NOTIFICATIONS, HISTORY
    }
    private ViewMode currentView = ViewMode.DASHBOARD;

    public void initialize() {
        notificationDAO = new NotificationDAO();
        userDAO = new UserDAO();
        
        // Session Fetch: Access the currently logged-in user's ID from your UserSession or Auth class.
        User loggedInUser = SessionManager.getLoggedInUser();
        if (loggedInUser != null) {
            this.currentUserId = loggedInUser.getId();
            this.currentUserName = loggedInUser.getFullName();
            updateWelcomeMessage();
            loadUserProfile();
        }
        
        setupNotificationList();
        
        // Default View: Ensure that when the user first logs in, the "Dashboard" profile is the first thing they see
        handleDashboard();
        
        startRealTimeUpdates();
    }

    public void initData(String fullName, int userId) {
        this.currentUserName = fullName;
        this.currentUserId = userId;
        updateWelcomeMessage();
        loadUserProfile();
        loadDataForCurrentView();
        updateNotificationBadge();
    }

    // Database Query: Implement a method loadUserProfile() that executes: SELECT department, role, sex, shift FROM users WHERE id = ?.
    private void loadUserProfile() {
        if (currentUserId > 0) {
            User user = userDAO.getUserDetails(currentUserId);
            if (user != null) {
                // UI Update: Set the text of the profile labels to match the results from the database.
                if (lblDepartment != null) lblDepartment.setText(user.getDepartment() != null ? user.getDepartment() : "Not Set");
                if (lblRole != null) lblRole.setText(user.getRole() != null ? user.getRole() : "Not Set");
                if (lblSex != null) lblSex.setText(user.getSex() != null ? user.getSex() : "Not Set");
                if (lblShift != null) lblShift.setText(user.getShift() != null ? user.getShift() : "Not Set");
            } else {
                if (lblDepartment != null) lblDepartment.setText("Not Set");
                if (lblRole != null) lblRole.setText("Not Set");
                if (lblSex != null) lblSex.setText("Not Set");
                if (lblShift != null) lblShift.setText("Not Set");
            }
        }
    }

    private void startRealTimeUpdates() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (currentUserId > 0) {
                    checkUserStatus();
                    loadDataForCurrentView();
                    updateNotificationBadge();
                }
            });
        }, 0, 5, TimeUnit.SECONDS);
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
        loadDataForCurrentView();
        updateNotificationBadge();
    }

    private void loadDataForCurrentView() {
        if (currentUserId <= 0) return;

        if (currentView == ViewMode.DASHBOARD) {
            profileView.setVisible(true);
            notificationsView.setVisible(false);
            return;
        }

        profileView.setVisible(false);
        notificationsView.setVisible(true);

        List<Notification> notifications;
        String title;

        switch (currentView) {
            case HISTORY:
                notifications = notificationDAO.getReadNotificationsForUser(currentUserId);
                title = "History (Read Messages)";
                break;
            case NOTIFICATIONS:
            default:
                List<Notification> unreadNotifications = notificationDAO.getUnreadNotificationsForUser(currentUserId);
                notifications = new ArrayList<>();
                
                // Add unread messages, excluding any that are locally seen (to avoid duplicates)
                for (Notification n : unreadNotifications) {
                    boolean isLocallySeen = sessionSeenNotifications.stream().anyMatch(sn -> sn.getId() == n.getId());
                    if (!isLocallySeen) {
                        notifications.add(n);
                    }
                }
                
                // Add locally seen messages
                notifications.addAll(sessionSeenNotifications);
                
                // Sort by date descending
                notifications.sort((n1, n2) -> {
                    if (n1.getSentAt() == null) return 1;
                    if (n2.getSentAt() == null) return -1;
                    return n2.getSentAt().compareTo(n1.getSentAt());
                });
                
                title = "New Notifications";
                break;
        }
        
        if (listTitle != null) listTitle.setText(title);
        
        int index = notificationsListView.getSelectionModel().getSelectedIndex();
        
        notificationsListView.setItems(FXCollections.observableArrayList(notifications));
        
        if (notifications.isEmpty()) {
            Label placeholder = new Label("No notifications to display");
            placeholder.setStyle("-fx-text-fill: #9ca3af;");
            notificationsListView.setPlaceholder(placeholder);
        }
        
        if (index >= 0 && index < notifications.size()) {
            notificationsListView.getSelectionModel().select(index);
        }
    }

    private void updateWelcomeMessage() {
        if (welcomeLabel != null) {
            String displayName = (currentUserName != null && !currentUserName.trim().isEmpty()) ? currentUserName : "User";
            welcomeLabel.setText("Welcome, " + displayName + "!");
        }
    }

    private void setupNotificationList() {
        notificationsListView.setCellFactory(param -> new ListCell<Notification>() {
            private final VBox contentBox = new VBox(10);
            private final Label titleLabel = new Label();
            private final Text messageText = new Text();
            private final HBox channelBox = new HBox(8);
            private final Label timestampLabel = new Label();
            private final Button deleteButton = new Button("Delete");
            private final Region spacer = new Region();
            private final HBox mainContent = new HBox(10, contentBox, spacer, deleteButton);

            {
                messageText.setWrappingWidth(500);
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                mainContent.setPadding(new Insets(15));
                contentBox.getChildren().addAll(titleLabel, messageText, channelBox, timestampLabel);
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

                    if (notification.getSentAt() != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");
                        timestampLabel.setText(notification.getSentAt().format(formatter));
                        timestampLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #9ca3af;");
                    } else {
                        timestampLabel.setText("");
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
                            loadDataForCurrentView();
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
                // Mark as seen locally
                selectedNotification.setSeen(true);
                sessionSeenNotifications.add(selectedNotification);
                
                // Trigger DB update
                markAsSeen(selectedNotification.getId());
                
                // Refresh UI to update styles
                notificationsListView.refresh();
                
                // Update badge
                updateNotificationBadge();
            }
        });
    }

    @FXML
    private void handleLogout() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        // Use the new switchScene method with maximized=false for login
        Main.switchScene("/view/login.fxml", "Instant Notification System - Login");
    }

    private void markAsSeen(int notificationId) {
        if (currentUserId > 0) {
            new Thread(() -> {
                notificationDAO.markNotificationAsSeen(currentUserId, notificationId);
                Platform.runLater(this::updateNotificationBadge);
            }).start();
        }
    }

    private void updateNotificationBadge() {
        if (notificationBadge != null && currentUserId > 0) {
            int unreadCount = notificationDAO.getUnreadNotificationCount(currentUserId);
            boolean hasUnread = unreadCount > 0;
            if (notificationBadgeContainer != null) {
                notificationBadgeContainer.setVisible(hasUnread);
            }
            notificationBadge.setText(String.valueOf(unreadCount));
        }
    }

    @FXML
    private void handleDashboard() {
        currentView = ViewMode.DASHBOARD;
        setActiveCard(dashboardCard);
        loadDataForCurrentView();
    }



    @FXML
    private void handleNotifications() {
        sessionSeenNotifications.clear(); // Clear session cache on view switch/refresh
        currentView = ViewMode.NOTIFICATIONS;
        setActiveCard(notificationsCard);
        loadDataForCurrentView();
    }

    @FXML
    private void handleHistory() {
        currentView = ViewMode.HISTORY;
        setActiveCard(historyCard);
        loadDataForCurrentView();
    }

    private void setActiveCard(Pane activeCard) {
        if (dashboardCard != null) dashboardCard.getStyleClass().remove("smart-card-active");
        if (notificationsCard != null) notificationsCard.getStyleClass().remove("smart-card-active");
        if (historyCard != null) historyCard.getStyleClass().remove("smart-card-active");

        if (activeCard != null) {
            activeCard.getStyleClass().add("smart-card-active");
        }
    }
}
