package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AdminDashboardController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private VBox sidebar;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnSendNotification;

    @FXML
    private Button btnViewUsers;

    @FXML
    private Button btnAnalytics;

    @FXML
    private Button btnLogout;

    @FXML
    private Label welcomeLabel;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Set welcome message
        welcomeLabel.setText("Welcome, Admin!");

        // Initialize sidebar buttons
        setupSidebarButtons();

        // Load default content (dashboard)
        loadDashboardContent();
    }

    private void setupSidebarButtons() {
        // Dashboard button
        btnDashboard.setOnAction(e -> loadDashboardContent());

        // Send Notification button
        btnSendNotification.setOnAction(e -> loadSendNotificationContent());

        // View Users button
        btnViewUsers.setOnAction(e -> loadUsersContent());

        // Analytics button
        btnAnalytics.setOnAction(e -> loadAnalyticsContent());

        // Logout button
        btnLogout.setOnAction(e -> SceneSwitcher.switchToLogin());
    }

    private void loadDashboardContent() {
        // Create dashboard content
        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new javafx.geometry.Insets(20));

        // Title
        Label titleLabel = new Label("Admin Dashboard Overview");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setPadding(new javafx.geometry.Insets(20, 0, 20, 0));

        // Stat cards
        addStatCard(statsGrid, "Total Users", "1,245", 0, 0);
        addStatCard(statsGrid, "Total Notifications", "5,678", 0, 1);
        addStatCard(statsGrid, "Seen Rate", "85%", 0, 2);
        addStatCard(statsGrid, "Pending Notifications", "23", 1, 0);
        addStatCard(statsGrid, "Active Today", "342", 1, 1);
        addStatCard(statsGrid, "Departments", "8", 1, 2);

        // Recent notifications
        Label recentLabel = new Label("Recent Notifications");
        recentLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

        TableView<String> notificationsTable = createNotificationsTable();

        dashboardContent.getChildren().addAll(titleLabel, statsGrid, recentLabel, notificationsTable);

        // Set content
        contentArea.getChildren().clear();
        contentArea.getChildren().add(dashboardContent);
    }

    private void addStatCard(GridPane grid, String title, String value, int row, int col) {
        VBox card = new VBox(10);
        card.setPadding(new javafx.geometry.Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefSize(200, 100);

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        card.getChildren().addAll(valueLabel, titleLabel);
        grid.add(card, col, row);
    }

    private TableView<String> createNotificationsTable() {
        TableView<String> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<String, String> titleCol = new TableColumn<>("Title");
        TableColumn<String, String> dateCol = new TableColumn<>("Date");
        TableColumn<String, String> statusCol = new TableColumn<>("Status");
        TableColumn<String, String> seenCol = new TableColumn<>("Seen %");

        titleCol.setPrefWidth(250);
        dateCol.setPrefWidth(150);
        statusCol.setPrefWidth(100);
        seenCol.setPrefWidth(100);

        table.getColumns().addAll(titleCol, dateCol, statusCol, seenCol);

        return table;
    }

    private void loadSendNotificationContent() {
        VBox notificationForm = new VBox(15);
        notificationForm.setPadding(new javafx.geometry.Insets(20));

        Label titleLabel = new Label("Send New Notification");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Title field
        Label titleInputLabel = new Label("Title:");
        TextField titleField = new TextField();
        titleField.setPromptText("Enter notification title");
        titleField.setPrefHeight(35);

        // Message field
        Label messageLabel = new Label("Message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter notification message");
        messageArea.setPrefHeight(150);

        // Target selection
        Label targetLabel = new Label("Target:");
        HBox targetBox = new HBox(10);
        ComboBox<String> targetType = new ComboBox<>();
        targetType.getItems().addAll("All Users", "Department", "Specific Users", "By Shift", "By Gender");
        targetType.setValue("All Users");

        ComboBox<String> departmentBox = new ComboBox<>();
        departmentBox.getItems().addAll("HR", "IT", "Finance", "Marketing");
        departmentBox.setVisible(false);

        targetType.setOnAction(e -> {
            departmentBox.setVisible("Department".equals(targetType.getValue()));
        });

        targetBox.getChildren().addAll(targetType, departmentBox);

        // Channels
        Label channelLabel = new Label("Channels:");
        HBox channelBox = new HBox(10);
        CheckBox smsCheck = new CheckBox("SMS");
        CheckBox emailCheck = new CheckBox("Email");
        CheckBox telegramCheck = new CheckBox("Telegram");
        CheckBox dashboardCheck = new CheckBox("Dashboard");
        dashboardCheck.setSelected(true);

        channelBox.getChildren().addAll(smsCheck, emailCheck, telegramCheck, dashboardCheck);

        // Schedule
        Label scheduleLabel = new Label("Schedule (Optional):");
        DatePicker scheduleDate = new DatePicker();
        ComboBox<String> scheduleTime = new ComboBox<>();
        scheduleTime.getItems().addAll("Immediate", "09:00", "10:00", "14:00", "16:00");
        scheduleTime.setValue("Immediate");

        HBox scheduleBox = new HBox(10);
        scheduleBox.getChildren().addAll(scheduleDate, scheduleTime);

        // Send button
        Button sendButton = new Button("Send Notification");
        sendButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5px;");

        sendButton.setOnAction(e -> {
            System.out.println("Notification sent!");
            // TODO: Add actual notification sending logic
        });

        notificationForm.getChildren().addAll(
                titleLabel, titleInputLabel, titleField,
                messageLabel, messageArea,
                targetLabel, targetBox,
                channelLabel, channelBox,
                scheduleLabel, scheduleBox,
                sendButton
        );

        contentArea.getChildren().clear();
        contentArea.getChildren().add(notificationForm);
    }

    private void loadUsersContent() {
        VBox usersContent = new VBox(20);
        usersContent.setPadding(new javafx.geometry.Insets(20));

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
        TableView<String> usersTable = new TableView<>();
        usersTable.setPrefHeight(400);

        TableColumn<String, String> nameCol = new TableColumn<>("Full Name");
        TableColumn<String, String> deptCol = new TableColumn<>("Department");
        TableColumn<String, String> phoneCol = new TableColumn<>("Phone");
        TableColumn<String, String> roleCol = new TableColumn<>("Role");
        TableColumn<String, String> statusCol = new TableColumn<>("Status");

        nameCol.setPrefWidth(200);
        deptCol.setPrefWidth(150);
        phoneCol.setPrefWidth(150);
        roleCol.setPrefWidth(100);
        statusCol.setPrefWidth(100);

        usersTable.getColumns().addAll(nameCol, deptCol, phoneCol, roleCol, statusCol);

        usersContent.getChildren().addAll(titleLabel, searchBox, usersTable);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(usersContent);
    }

    private void loadAnalyticsContent() {
        VBox analyticsContent = new VBox(20);
        analyticsContent.setPadding(new javafx.geometry.Insets(20));

        Label titleLabel = new Label("Analytics Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Charts placeholder
        HBox chartsBox = new HBox(20);

        VBox chart1 = createChartCard("Notification Delivery Rate", "85%");
        VBox chart2 = createChartCard("Channel Distribution", "SMS: 40%, Email: 30%, Dashboard: 30%");
        VBox chart3 = createChartCard("Peak Activity Times", "10:00 AM, 2:00 PM");

        chartsBox.getChildren().addAll(chart1, chart2, chart3);

        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);

        addAnalyticStat(statsGrid, "Total Users", "1,245", "↗ 12%", 0, 0);
        addAnalyticStat(statsGrid, "Total Notifications", "5,678", "↗ 8%", 0, 1);
        addAnalyticStat(statsGrid, "Avg Seen Time", "2.3 min", "↘ 0.5 min", 0, 2);
        addAnalyticStat(statsGrid, "SMS Success", "92%", "↗ 2%", 1, 0);
        addAnalyticStat(statsGrid, "Email Open Rate", "78%", "↗ 5%", 1, 1);
        addAnalyticStat(statsGrid, "User Engagement", "64%", "↗ 7%", 1, 2);

        analyticsContent.getChildren().addAll(titleLabel, chartsBox, statsGrid);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(analyticsContent);
    }

    private VBox createChartCard(String title, String data) {
        VBox chartCard = new VBox(10);
        chartCard.setPadding(new javafx.geometry.Insets(15));
        chartCard.setPrefSize(250, 200);
        chartCard.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Placeholder for chart (could be replaced with actual JavaFX chart)
        Pane chartPlaceholder = new Pane();
        chartPlaceholder.setPrefSize(220, 120);
        chartPlaceholder.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; " +
                "-fx-border-radius: 5px;");

        Label dataLabel = new Label(data);
        dataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        chartCard.getChildren().addAll(titleLabel, chartPlaceholder, dataLabel);
        return chartCard;
    }

    private void addAnalyticStat(GridPane grid, String title, String value, String trend, int row, int col) {
        VBox statCard = new VBox(5);
        statCard.setPadding(new javafx.geometry.Insets(15));
        statCard.setPrefSize(200, 100);
        statCard.setStyle("-fx-background-color: white; -fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        HBox valueBox = new HBox(10);
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #3498db;");

        Label trendLabel = new Label(trend);
        if (trend.contains("↗")) {
            trendLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        } else {
            trendLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }

        valueBox.getChildren().addAll(valueLabel, trendLabel);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        statCard.getChildren().addAll(valueBox, titleLabel);
        grid.add(statCard, col, row);
    }
}