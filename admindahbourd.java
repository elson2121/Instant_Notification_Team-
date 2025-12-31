Nati Class Group, [31/12/2025 09:39]
package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.Main;
import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.model.UserNotificationDetail;
import com.instantnotificationsystem.service.InfobipEmailService;
import com.instantnotificationsystem.service.InfobipSMSService;
import com.instantnotificationsystem.service.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardController {

    //<editor-fold desc="FXML Fields">
    @FXML private Button btnDashboard;
    @FXML private Button btnUsers;
    @FXML private Button btnNotifications;
    @FXML private Button btnLogout;
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;
    @FXML private TextField subjectField;
    @FXML private TextArea messageArea;
    @FXML private CheckBox emailCheckbox;
    @FXML private CheckBox smsCheckbox = new CheckBox();
    @FXML private VBox dashboardView;
    @FXML private FlowPane statsPane;
    @FXML private TableView<Notification> recentNotificationsTable; // Corrected to use Notification model
    
    // New Filter Fields
    @FXML private ComboBox<String> departmentCombo;
    @FXML private ComboBox<String> roleCombo;
    @FXML private ComboBox<String> sexCombo;
    @FXML private ComboBox<String> shiftCombo;
    
    // Scheduling Fields
    @FXML private DatePicker scheduleDate;
    @FXML private Spinner<Integer> scheduleTime;
    //</editor-fold>

    private UserDAO userDAO;
    private NotificationDAO notificationDAO;
    private InfobipSMSService infobipSMSService;
    private InfobipEmailService infobipEmailService;
    private int currentAdminId;
    private String currentAdminName;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        notificationDAO = new NotificationDAO();
        infobipSMSService = new InfobipSMSService();
        infobipEmailService = new InfobipEmailService();

        if (welcomeLabel != null) {
            User loggedInAdmin = SessionManager.getLoggedInUser();
            welcomeLabel.setText("Welcome, " + (loggedInAdmin != null ? loggedInAdmin.getFullName() : "Administrator") + "!");
        }

        // Set SMS checkbox to be checked by default
        if (smsCheckbox != null) {
            smsCheckbox.setSelected(true);
        }

        setupSidebarButtons();
        loadDashboardContent();
    }

    public void initData(String fullName, int userId) {
        this.currentAdminName = fullName;
        this.currentAdminId = userId;
        welcomeLabel.setText("Welcome, " + fullName + "!");
    }

    private void setupSidebarButtons() {
        btnDashboard.setOnAction(e -> {
            setActiveButton(btnDashboard);
            loadDashboardContent();
        });
        btnUsers.setOnAction(e -> {
            setActiveButton(btnUsers);
            loadUsersContent(null);
        });
        btnNotifications.setOnAction(e -> {
            setActiveButton(btnNotifications);
            loadSendNotificationContent();
        });
        btnLogout.setOnAction(e -> {
            SessionManager.clear();
            // Use the new switchScene method with maximized=false for login
            Main.switchScene("/view/login.fxml", "Instant Notification System - Login");
        });
        setActiveButton(btnDashboard);
    }

Nati Class Group, [31/12/2025 09:39]
private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("sidebar-button-active");
        btnUsers.getStyleClass().remove("sidebar-button-active");
        btnNotifications.getStyleClass().remove("sidebar-button-active");
        activeButton.getStyleClass().add("sidebar-button-active");
    }

    private void showContent(Node content) {
        dashboardView.setVisible(false);
        dashboardView.setManaged(false);
        contentArea.getChildren().setAll(content);
    }

    private void loadDashboardContent() {
        if (contentArea == null) return;

        dashboardView.setVisible(true);
        dashboardView.setManaged(true);
        contentArea.getChildren().setAll(dashboardView);

        statsPane.getChildren().clear();

        StackPane usersCard = addStatCard(statsPane, "Total Users", String.valueOf(userDAO.getTotalUserCount()), "👥");
        StackPane sentCard = addStatCard(statsPane, "Notifications Sent", String.valueOf(notificationDAO.getSentCount()), "✉️");
        StackPane deliveredCard = addStatCard(statsPane, "Total Delivered", String.valueOf(notificationDAO.getCountByDeliveryStatus("Delivered")), "🚚");
        StackPane seenCard = addStatCard(statsPane, "Total Seen", String.valueOf(notificationDAO.getCountBySeenStatus(true)), "👀");
        StackPane unseenCard = addStatCard(statsPane, "Total Unseen", String.valueOf(notificationDAO.getCountBySeenStatus(false)), "🙈");
        StackPane userManagementCard = addStatCard(statsPane, "User Management", "3", "⚙️");


        usersCard.setOnMouseClicked(e -> {
            setActiveButton(btnUsers);
            loadUsersContent(null);
        });
        sentCard.setOnMouseClicked(e -> {
            setActiveButton(btnNotifications);
            loadNotificationsByStatus(null); // All notifications
        });
        deliveredCard.setOnMouseClicked(e -> {
            setActiveButton(btnNotifications);
            loadNotificationsByStatus("Delivered");
        });
        seenCard.setOnMouseClicked(e -> {
            setActiveButton(btnNotifications);
            loadNotificationsByStatus("Seen");
        });
        unseenCard.setOnMouseClicked(e -> {
            setActiveButton(btnNotifications);
            loadNotificationsByStatus("Unseen");
        });
        userManagementCard.setOnMouseClicked(e -> {
            // Navigate to user management view
            loadUserManagementContent();
        });

        populateRecentNotificationsTable();
    }

    private StackPane addStatCard(Pane parent, String title, String value, String icon) {
        StackPane card = new StackPane();
        card.getStyleClass().add("stat-card");
        card.setCursor(Cursor.HAND);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("card-icon");
        StackPane.setAlignment(iconLabel, Pos.CENTER);
        StackPane.setMargin(iconLabel, new Insets(15));

        VBox textContainer = new VBox(5);
        textContainer.setAlignment(Pos.BOTTOM_LEFT);
        textContainer.setPadding(new Insets(20));
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("value-label");
        Label titleLabel = new Label(title);
        textContainer.getChildren().addAll(valueLabel, titleLabel);

        card.getChildren().addAll(textContainer, iconLabel);
        parent.getChildren().add(card);
        return card;
    }

    private void populateRecentNotificationsTable() {
        // Ensure columns are created only once
        if (recentNotificationsTable.getColumns().isEmpty()) {
            TableColumn<Notification, String> titleCol = new TableColumn<>("Title");
            titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

Nati Class Group, [31/12/2025 09:39]
// Correctly define the TableColumn type and set the PropertyValueFactory
            TableColumn<Notification, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status")); // FORCE BINDING
            statusCol.getStyleClass().add("status-column");
            
            statusCol.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : getItem());
                    getStyleClass().removeAll("status-delivered", "status-sent", "status-failed");
                    if (item != null && !empty) {
                        switch (item) {
                            case "Delivered":
                                getStyleClass().add("status-delivered");
                                break;
                            case "Sent":
                                getStyleClass().add("status-sent");
                                break;
                            case "Failed":
                                getStyleClass().add("status-failed");
                                break;
                        }
                    }
                }
            });

            TableColumn<Notification, Void> seenProgressCol = new TableColumn<>("Seen Progress");
            seenProgressCol.getStyleClass().add("progress-column");
            seenProgressCol.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        getStyleClass().removeAll("progress-low", "progress-medium", "progress-high");
                    } else {
                        Notification notification = getTableView().getItems().get(getIndex());
                        int seen = notification.getSeenCount();
                        int total = notification.getTotalRecipients();
                        
                        if (total > 0) {
                            double percentage = ((double) seen / total) * 100;
                            setText(String.format("%.1f%%", percentage));
                            
                            getStyleClass().removeAll("progress-low", "progress-medium", "progress-high");
                            if (percentage < 30) {
                                getStyleClass().add("progress-low");
                            } else if (percentage <= 70) {
                                getStyleClass().add("progress-medium");
                            } else {
                                getStyleClass().add("progress-high");
                            }
                        } else {
                            setText("0.0%");
                            getStyleClass().add("progress-low");
                        }
                    }
                }
            });

            recentNotificationsTable.getColumns().addAll(titleCol, statusCol, seenProgressCol);
        }

        // Fetch real notification data from the database
        List<Notification> notifications = notificationDAO.getAllNotifications();
        recentNotificationsTable.setItems(FXCollections.observableArrayList(notifications));
    }

    @FXML
    private void handleSendNotification() {
        String subject = subjectField.getText().trim();
        String message = messageArea.getText().trim();

Nati Class Group, [31/12/2025 09:39]
if (subject.isEmpty()  message.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in both subject and message fields.").showAndWait();
            return;
        }

        // Get target users based on filters
        String department = departmentCombo.getValue();
        String role = roleCombo.getValue();
        String sex = sexCombo.getValue();
        String shift = shiftCombo.getValue();
        List<User> targetUsers = userDAO.getUsersByCriteria(
                "All Departments".equals(department) ? null : department,
                "All Roles".equals(role) ? null : role,
                "All".equals(sex) ? null : sex,
                "All Shifts".equals(shift) ? null : shift
        );

        if (targetUsers.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No users match the selected criteria.").showAndWait();
            return;
        }

        int senderId = SessionManager.getLoggedInUser() != null ? SessionManager.getLoggedInUser().getId() : 1;
        Notification newNotification = new Notification();
        newNotification.setTitle(subject);
        newNotification.setMessage(message);
        newNotification.setSenderId(senderId);
        newNotification.setStatus("Sent");
