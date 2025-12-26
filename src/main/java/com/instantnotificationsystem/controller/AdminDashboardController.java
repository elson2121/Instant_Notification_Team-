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

import java.util.List;

public class AdminDashboardController {

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnUsers;

    @FXML
    private Button btnNotifications;

    @FXML
    private Button btnAnalytics;

    @FXML
    private Button btnSettings;

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
        welcomeLabel.setText("Welcome, Admin!");

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

        // Analytics button
        if (btnAnalytics != null) {
            btnAnalytics.setOnAction(e -> {
                setActiveButton(btnAnalytics);
                loadAnalyticsContent();
            });
        }

        // Settings button
        if (btnSettings != null) {
            btnSettings.setOnAction(e -> {
                setActiveButton(btnSettings);
                // loadSettingsContent(); // Placeholder
            });
        }

        // Logout button
        if (btnLogout != null) {
            btnLogout.setOnAction(e -> SceneSwitcher.switchToLogin());
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
        if (btnAnalytics != null) btnAnalytics.getStyleClass().remove("active-sidebar-button");
        if (btnSettings != null) btnSettings.getStyleClass().remove("active-sidebar-button");

        if (activeButton != null) {
            activeButton.getStyleClass().add("active-sidebar-button");
        }
    }

    private void loadDashboardContent() {
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
        
        // Stat cards with full color background and icons
        addStatCard(statsGrid, "Total Users", "1,245", 0, 0, "#2ecc71", "👥"); // Green
        addStatCard(statsGrid, "Notifications Sent", "5,678", 0, 1, "#3498db", "✉️"); // Blue
        addStatCard(statsGrid, "Active Users", "342", 0, 2, "#e67e22", "⚡"); // Orange
        addStatCard(statsGrid, "Pending Alerts", "14", 0, 3, "#9b59b6", "⚠️"); // Purple

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
        card.setPrefWidth(250);
        card.setPrefHeight(100);

        // Icon container
        StackPane iconPane = new StackPane();
        iconPane.setPrefSize(50, 50);
        iconPane.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 50%;");
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        iconPane.getChildren().add(iconLabel);

        // Text container
        VBox textContainer = new VBox(5);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.9);");
        
        textContainer.getChildren().addAll(valueLabel, titleLabel);

        card.getChildren().addAll(iconPane, textContainer);
        grid.add(card, col, row);
    }
    
    private VBox createMiniSendForm() {
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label titleLabel = new Label("Send Notification");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        TextField titleField = new TextField();
        titleField.setPromptText("Notification Title");
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Message content...");
        messageArea.setPrefRowCount(3);
        
        ComboBox<String> sendToCombo = new ComboBox<>();
        sendToCombo.getItems().addAll("All Users", "Specific Department", "Specific User");
        sendToCombo.setValue("All Users");
        sendToCombo.setMaxWidth(Double.MAX_VALUE);
        
        HBox channelBox = new HBox(10);
        ToggleButton emailBtn = new ToggleButton("Email");
        ToggleButton smsBtn = new ToggleButton("SMS");
        ToggleButton pushBtn = new ToggleButton("Push Notification");
        
        channelBox.getChildren().addAll(emailBtn, smsBtn, pushBtn);
        
        Button sendBtn = new Button("Send Notification");
        sendBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        sendBtn.setMaxWidth(Double.MAX_VALUE);
        
        formBox.getChildren().addAll(titleLabel, titleField, messageArea, sendToCombo, channelBox, sendBtn);
        return formBox;
    }
    
    private VBox createNotificationChart() {
        VBox chartBox = new VBox(10);
        chartBox.setPadding(new Insets(15));
        chartBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        Label titleLabel = new Label("Notification Overview");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setPrefHeight(300); // Increased height slightly for better visibility
        lineChart.setLegendVisible(true);
        
        XYChart.Series<String, Number> sentSeries = new XYChart.Series<>();
        sentSeries.setName("Sent");
        sentSeries.getData().add(new XYChart.Data<>("Mon", 20));
        sentSeries.getData().add(new XYChart.Data<>("Tue", 35));
        sentSeries.getData().add(new XYChart.Data<>("Wed", 40));
        sentSeries.getData().add(new XYChart.Data<>("Thu", 30));
        sentSeries.getData().add(new XYChart.Data<>("Fri", 50));
        
        XYChart.Series<String, Number> deliveredSeries = new XYChart.Series<>();
        deliveredSeries.setName("Delivered");
        deliveredSeries.getData().add(new XYChart.Data<>("Mon", 18));
        deliveredSeries.getData().add(new XYChart.Data<>("Tue", 32));
        deliveredSeries.getData().add(new XYChart.Data<>("Wed", 38));
        deliveredSeries.getData().add(new XYChart.Data<>("Thu", 28));
        deliveredSeries.getData().add(new XYChart.Data<>("Fri", 48));
        
        XYChart.Series<String, Number> failedSeries = new XYChart.Series<>();
        failedSeries.setName("Failed");
        failedSeries.getData().add(new XYChart.Data<>("Mon", 2));
        failedSeries.getData().add(new XYChart.Data<>("Tue", 3));
        failedSeries.getData().add(new XYChart.Data<>("Wed", 2));
        failedSeries.getData().add(new XYChart.Data<>("Thu", 2));
        failedSeries.getData().add(new XYChart.Data<>("Fri", 2));
        
        lineChart.getData().addAll(sentSeries, deliveredSeries, failedSeries);
        
        chartBox.getChildren().addAll(titleLabel, lineChart);
        return chartBox;
    }
    
    private VBox createRecentNotificationsBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Label titleLabel = new Label("Recent Notifications");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        TableView<NotificationMock> table = new TableView<>();
        table.setPrefHeight(250); // Increased height slightly
        
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
        VBox notificationForm = new VBox(15);
        notificationForm.setPadding(new Insets(20));

        Label titleLabel = new Label("Send New Notification");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Reusing the mini form logic but expanded if needed, for now just placeholder
        notificationForm.getChildren().add(titleLabel);
        notificationForm.getChildren().add(createMiniSendForm());
        
        contentArea.getChildren().clear();
        contentArea.getChildren().add(notificationForm);
    }

    private void loadUsersContent() {
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

    private void loadAnalyticsContent() {
        VBox analyticsContent = new VBox(20);
        analyticsContent.setPadding(new Insets(20));

        Label titleLabel = new Label("Analytics Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        analyticsContent.getChildren().addAll(titleLabel, createNotificationChart());

        contentArea.getChildren().clear();
        contentArea.getChildren().add(analyticsContent);
    }
}