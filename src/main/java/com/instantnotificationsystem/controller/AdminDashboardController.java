package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.model.UserNotificationDetail;
import com.instantnotificationsystem.service.SessionManager;
import com.instantnotificationsystem.utils.SceneSwitcher;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    @FXML private CheckBox smsCheckbox;
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

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        notificationDAO = new NotificationDAO();

        if (welcomeLabel != null) {
            User loggedInAdmin = SessionManager.getLoggedInUser();
            welcomeLabel.setText("Welcome, " + (loggedInAdmin != null ? loggedInAdmin.getFullName() : "Administrator") + "!");
        }

        setupSidebarButtons();
        loadDashboardContent();
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
            try {
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                SceneSwitcher.switchScene(stage, "/view/login.fxml");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        setActiveButton(btnDashboard);
    }

    private void setActiveButton(Button activeButton) {
        btnDashboard.getStyleClass().remove("active-sidebar-button");
        btnUsers.getStyleClass().remove("active-sidebar-button");
        btnNotifications.getStyleClass().remove("active-sidebar-button");
        activeButton.getStyleClass().add("active-sidebar-button");
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

        StackPane usersCard = addStatCard(statsPane, "Total Users", String.valueOf(userDAO.getTotalUserCount()), "card-green", "👥");
        StackPane sentCard = addStatCard(statsPane, "Notifications Sent", String.valueOf(notificationDAO.getSentCount()), "card-blue", "✉️");
        StackPane deliveredCard = addStatCard(statsPane, "Total Delivered", String.valueOf(notificationDAO.getCountByDeliveryStatus("Delivered")), "card-yellow", "🚚");
        StackPane seenCard = addStatCard(statsPane, "Total Seen", String.valueOf(notificationDAO.getCountBySeenStatus(true)), "card-teal", "👀");
        StackPane unseenCard = addStatCard(statsPane, "Total Unseen", String.valueOf(notificationDAO.getCountBySeenStatus(false)), "card-red", "🙈");

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

        populateRecentNotificationsTable();
    }

    private StackPane addStatCard(Pane parent, String title, String value, String styleClass, String icon) {
        StackPane card = new StackPane();
        card.getStyleClass().addAll("stat-card", styleClass);
        card.setCursor(Cursor.HAND);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("card-icon");
        StackPane.setAlignment(iconLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(iconLabel, new Insets(15));

        VBox textContainer = new VBox(5);
        textContainer.setAlignment(Pos.BOTTOM_LEFT);
        textContainer.setPadding(new Insets(20));
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
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

            // Correctly define the TableColumn type and set the PropertyValueFactory
            TableColumn<Notification, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status")); // FORCE BINDING
            statusCol.setStyle("-fx-alignment: CENTER-LEFT;"); // Center align content
            
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
            seenProgressCol.setStyle("-fx-alignment: CENTER-LEFT;"); // Center align content
            seenProgressCol.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        Notification notification = getTableView().getItems().get(getIndex());
                        int seen = notification.getSeenCount();
                        int total = notification.getTotalRecipients();
                        
                        if (total > 0) {
                            double percentage = ((double) seen / total) * 100;
                            setText(String.format("%.1f%%", percentage));
                            
                            if (percentage < 30) {
                                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            } else if (percentage <= 70) {
                                setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            } else {
                                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            }
                        } else {
                            setText("0.0%");
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
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
        int senderId = 1; // Admin user ID
        Notification newNotification = new Notification();
        newNotification.setTitle(subjectField.getText());
        newNotification.setMessage(messageArea.getText());
        newNotification.setSenderId(senderId);

        String notificationType = emailCheckbox.isSelected() && smsCheckbox.isSelected() ? "BOTH"
                                : emailCheckbox.isSelected() ? "Email" : "SMS";
        newNotification.setNotificationType(notificationType);

        List<String> channels = new ArrayList<>();
        if (emailCheckbox.isSelected()) channels.add("Email");
        if (smsCheckbox.isSelected()) channels.add("SMS");
        newNotification.setChannels(channels);

        // Scheduling Logic
        if (scheduleDate.getValue() != null) {
            LocalDate date = scheduleDate.getValue();
            int hour = scheduleTime.getValue() != null ? scheduleTime.getValue() : 0;
            LocalDateTime scheduledDateTime = LocalDateTime.of(date, LocalTime.of(hour, 0));
            newNotification.setScheduledAt(scheduledDateTime);
        }

        int notificationId = notificationDAO.createNotification(newNotification);

        if (notificationId > 0) {
            // Filter Logic
            String department = departmentCombo.getValue();
            String role = roleCombo.getValue();
            String sex = sexCombo.getValue();
            String shift = shiftCombo.getValue();

            List<User> targetUsers = userDAO.getUsersByCriteria(department, role, sex, shift);
            notificationDAO.createUserNotifications(notificationId, targetUsers);
            
            new Alert(Alert.AlertType.INFORMATION, "Notification sent successfully!").showAndWait();
            loadDashboardContent();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to save notification.").showAndWait();
        }
    }

    private void loadSendNotificationContent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin_notifications.fxml"));
            loader.setController(this);
            VBox notificationView = loader.load();
            
            // Initialize ComboBoxes and Spinner after loading FXML
            initializeFilterControls();
            
            showContent(notificationView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeFilterControls() {
        if (departmentCombo != null) {
            departmentCombo.setItems(FXCollections.observableArrayList("All Departments", "HR", "IT", "Sales", "Marketing"));
            departmentCombo.getSelectionModel().selectFirst();
        }
        if (roleCombo != null) {
            roleCombo.setItems(FXCollections.observableArrayList("All Roles", "Manager", "Employee", "Intern"));
            roleCombo.getSelectionModel().selectFirst();
        }
        if (sexCombo != null) {
            sexCombo.setItems(FXCollections.observableArrayList("All", "Male", "Female"));
            sexCombo.getSelectionModel().selectFirst();
        }
        if (shiftCombo != null) {
            shiftCombo.setItems(FXCollections.observableArrayList("All Shifts", "Morning", "Night"));
            shiftCombo.getSelectionModel().selectFirst();
        }
        if (scheduleTime != null) {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
            scheduleTime.setValueFactory(valueFactory);
        }
    }

    private void loadUsersContent(Boolean seenStatus) {
        TableView<User> usersTable = new TableView<>();
        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
        usersTable.getColumns().addAll(nameCol, deptCol);

        List<User> users;
        String title;

        if (seenStatus == null) {
            users = userDAO.getAllUsers();
            title = "All Users";
        } else {
            users = userDAO.getUsersByNotificationSeenStatus(seenStatus);
            title = seenStatus ? "Users Who Have Seen Notifications" : "Users With Unseen Notifications";
        }
        usersTable.setItems(FXCollections.observableArrayList(users));

        VBox userContentView = createContentViewWithBack(title, usersTable);
        showContent(userContentView);
    }

    private void loadNotificationsByStatus(String status) {
        if ("Seen".equals(status) || "Unseen".equals(status)) {
            loadSeenUnseenNotifications(status);
            return;
        }

        TableView<Notification> table = new TableView<>();
        TableColumn<Notification, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Notification, String> msgCol = new TableColumn<>("Message");
        msgCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        table.getColumns().addAll(titleCol, msgCol);

        List<Notification> notifications;
        String title;

        if (status == null) {
            notifications = notificationDAO.getAllNotifications();
            title = "All Sent Notifications";
        } else {
            notifications = notificationDAO.getNotificationsByDeliveryStatus(status);
            title = "Notifications: " + status;
        }

        table.setItems(FXCollections.observableArrayList(notifications));

        VBox contentView = createContentViewWithBack(title, table);
        showContent(contentView);
    }

    private void loadSeenUnseenNotifications(String status) {
        TableView<UserNotificationDetail> table = new TableView<>();
        
        TableColumn<UserNotificationDetail, String> userCol = new TableColumn<>("User Name");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        
        TableColumn<UserNotificationDetail, String> titleCol = new TableColumn<>("Notification Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("notificationTitle"));
        
        TableColumn<UserNotificationDetail, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setStyle("-fx-alignment: CENTER-LEFT;");
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Seen".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("Unseen".equalsIgnoreCase(item)) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        table.getColumns().addAll(userCol, titleCol, statusCol);

        boolean isSeen = "Seen".equals(status);
        List<UserNotificationDetail> details = notificationDAO.getUserNotificationDetailsBySeenStatus(isSeen);
        String title = "Notifications: " + status;

        table.setItems(FXCollections.observableArrayList(details));

        VBox contentView = createContentViewWithBack(title, table);
        showContent(contentView);
    }

    private VBox createContentViewWithBack(String title, Node content) {
        Button backButton = new Button("Back to Overview");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> {
            setActiveButton(btnDashboard);
            loadDashboardContent();
        });

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(titleLabel, spacer, backButton);

        VBox container = new VBox(20, header, content);
        container.setPadding(new Insets(20));
        return container;
    }
}