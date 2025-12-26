package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnNotifications;

    @FXML
    private Button btnLogout;

    @FXML
    private Label welcomeLabel;

    @FXML
    private StackPane contentArea;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();

        // Set welcome message
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, Admin!");
        }

        // Initialize sidebar buttons
        setupSidebarButtons();

        // Load default content (dashboard)
        loadDashboardContent();
    }

    private void setupSidebarButtons() {
        // Dashboard button
        if (btnDashboard != null) {
            btnDashboard.setOnAction(e -> {
                setActiveButton(btnDashboard);
                loadDashboardContent();
            });
        }

        // Users button
        if (btnUsers != null) {
            btnUsers.setOnAction(e -> {
                setActiveButton(btnUsers);
                loadUsersContent();
            });
        }

        // Notifications button
        if (btnNotifications != null) {
            btnNotifications.setOnAction(e -> {
                setActiveButton(btnNotifications);
                loadSendNotificationContent();
            });
        }

        // Logout button
        if (btnLogout != null) {
            btnLogout.setOnAction(e -> {
                try {
                    Stage stage = (Stage) btnLogout.getScene().getWindow();
                    SceneSwitcher.switchScene(stage, "/view/login.fxml");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
        
        // Set initial active button
        if (btnDashboard != null) {
            setActiveButton(btnDashboard);
        }
    }
    
    private void setActiveButton(Button activeButton) {
        if (btnDashboard != null) btnDashboard.getStyleClass().remove("active-sidebar-button");
        if (btnUsers != null) btnUsers.getStyleClass().remove("active-sidebar-button");
        if (btnNotifications != null) btnNotifications.getStyleClass().remove("active-sidebar-button");

        if (activeButton != null) {
            activeButton.getStyleClass().add("active-sidebar-button");
        }
    }

    private void loadDashboardContent() {
        if (contentArea == null) return;

        // Create dashboard content
        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(20));

        // Breadcrumb
        Label breadcrumb = new Label("Admin / Dashboard");
        breadcrumb.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setAlignment(Pos.CENTER); // Center the grid
        
        // Stat cards with full color background and icons
        // Removed Active Users and Pending Alerts
        addStatCard(statsGrid, "Total Users", "1,245", 0, 0, "#2ecc71", "👥"); // Green
        addStatCard(statsGrid, "Notifications Sent", "5,678", 0, 1, "#3498db", "✉️"); // Blue

        // Main content layout
        VBox mainContent = new VBox(20);
        mainContent.setPrefWidth(1000);

        // Recent notifications (Full Width)
        VBox recentNotificationsBox = createRecentNotificationsBox();
        recentNotificationsBox.setMaxWidth(Double.MAX_VALUE);
        
        mainContent.getChildren().addAll(recentNotificationsBox);

        dashboardContent.getChildren().addAll(breadcrumb, statsGrid, mainContent);

        // Set content
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);
    }

    private void addStatCard(GridPane grid, String title, String value, int row, int col, String colorHex, String icon) {
        HBox card = new HBox(15);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        // Full color background
        card.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        card.setPrefWidth(400); // Increased width for better centering
        card.setPrefHeight(120); // Increased height slightly

        // Icon container
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(60, 60);
        iconPane.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 50%;");
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px; -fx-text-fill: white;");
        iconPane.getChildren().add(iconLabel);

        // Text container
        VBox textContainer = new VBox(5);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: rgba(255,255,255,0.9);");
        
        textContainer.getChildren().addAll(valueLabel, titleLabel);

        card.getChildren().addAll(iconPane, textContainer);
        grid.add(card, col, row);
    }
    
    private VBox createMiniSendForm() {
        VBox formBox = new VBox(20); // Increased spacing
        formBox.setPadding(new Insets(30)); // Increased padding
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        formBox.setAlignment(Pos.TOP_CENTER); // Center alignment
        
        Label titleLabel = new Label("Send Notification");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Input Container
        VBox inputContainer = new VBox(15);
        inputContainer.setMaxWidth(600); // Limit width for better readability
        inputContainer.setAlignment(Pos.CENTER_LEFT);

        // Title Field
        VBox titleBox = new VBox(5);
        Label subjectLabel = new Label("Subject");
        subjectLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter Subject...");
        titleField.setPrefHeight(40);
        titleField.setStyle("-fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        titleBox.getChildren().addAll(subjectLabel, titleField);
        
        // Message Area
        VBox messageBox = new VBox(5);
        Label messageLabel = new Label("Message");
        messageLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type message here...");
        messageArea.setPrefRowCount(5);
        messageArea.setStyle("-fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");
        messageBox.getChildren().addAll(messageLabel, messageArea);
        
        // Recipient Selection
        VBox recipientBox = new VBox(5);
        Label recipientLabel = new Label("Send To");
        recipientLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        ComboBox<String> sendToCombo = new ComboBox<>();
        sendToCombo.getItems().addAll("All Users", "Specific Department", "Specific User");
        sendToCombo.setValue("All Users");
        sendToCombo.setMaxWidth(Double.MAX_VALUE);
        sendToCombo.setPrefHeight(40);
        sendToCombo.setStyle("-fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-color: white;");
        recipientBox.getChildren().addAll(recipientLabel, sendToCombo);

        // Delivery Channels (Checkboxes)
        HBox channelBox = new HBox(20);
        channelBox.setAlignment(Pos.CENTER_LEFT);
        Label channelLabel = new Label("Delivery Method:");
        channelLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e;");
        
        CheckBox emailCheck = new CheckBox("Email");
        emailCheck.setStyle("-fx-font-size: 14px;");
        
        CheckBox smsCheck = new CheckBox("SMS");
        smsCheck.setStyle("-fx-font-size: 14px;");
        
        channelBox.getChildren().addAll(channelLabel, emailCheck, smsCheck);
        
        inputContainer.getChildren().addAll(titleBox, messageBox, recipientBox, channelBox);
        
        // Send Button
        Button sendBtn = new Button("Send Notification");
        sendBtn.setStyle("-fx-background-color: #2D62ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 8; -fx-cursor: hand;");
        sendBtn.setPrefWidth(200);
        sendBtn.setPrefHeight(45);
        
        formBox.getChildren().addAll(titleLabel, inputContainer, sendBtn);
        return formBox;
    }
    
    private VBox createRecentNotificationsBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titleLabel = new Label("Recent Notifications");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        TableView<NotificationMock> table = new TableView<>();
        table.setPrefHeight(300); // Increased height
        
        TableColumn<NotificationMock, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<NotificationMock, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellFactory(column -> new TableCell<NotificationMock, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Delivered")) {
                        setTextFill(Color.GREEN);
                    } else if (item.equals("Failed")) {
                        setTextFill(Color.RED);
                    } else {
                        setTextFill(Color.BLUE);
                    }
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
        
        table.getColumns().addAll(titleCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        ObservableList<NotificationMock> data = FXCollections.observableArrayList(
            new NotificationMock("System Update", "Delivered"),
            new NotificationMock("Meeting Alert", "Sent"),
            new NotificationMock("Server Down", "Failed"),
            new NotificationMock("Welcome Email", "Delivered")
        );
        
        table.setItems(data);
        
        box.getChildren().addAll(titleLabel, table);
        return box;
    }
    
    // Mock class for table
    public static class NotificationMock {
        private String title;
        private String status;
        
        public NotificationMock(String title, String status) {
            this.title = title;
            this.status = status;
        }
        
        public String getTitle() { return title; }
        public String getStatus() { return status; }
    }

    private void loadSendNotificationContent() {
        if (contentArea == null) return;

        VBox notificationForm = new VBox(15);
        notificationForm.setPadding(new Insets(20));
        notificationForm.setAlignment(Pos.TOP_CENTER); // Center the form in the view

        // Reusing the mini form logic but expanded if needed, for now just placeholder
        notificationForm.getChildren().add(createMiniSendForm());
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(notificationForm);
    }

    private void loadUsersContent() {
        if (contentArea == null) return;

        VBox usersContent = new VBox(20);
        usersContent.setPadding(new Insets(20));

        Label titleLabel = new Label("User Management");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Search bar
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(300);
        Button searchButton = new Button("Search");

        searchBox.getChildren().addAll(searchField, searchButton);

        // Users table
        TableView<User> usersTable = new TableView<>();
        usersTable.setPrefHeight(400);

        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<User, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        nameCol.setPrefWidth(200);
        deptCol.setPrefWidth(150);
        phoneCol.setPrefWidth(150);
        roleCol.setPrefWidth(100);

        usersTable.getColumns().addAll(nameCol, deptCol, phoneCol, roleCol);

        // Load data from DB
        List<User> userList = userDAO.getAllUsers();
        ObservableList<User> observableUserList = FXCollections.observableArrayList(userList);
        usersTable.setItems(observableUserList);

        usersContent.getChildren().addAll(titleLabel, searchBox, usersTable);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(usersContent);
    }
}