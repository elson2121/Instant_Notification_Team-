package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.List;

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
        dashboardContent.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Admin Dashboard Overview");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Stats grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(20);
        statsGrid.setPadding(new Insets(20, 0, 20, 0));

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
        card.setPadding(new Insets(15));
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
        notificationForm.setPadding(new Insets(20));

        Label titleLabel = new Label("Send New Notification");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // ... (rest of the method is unchanged)
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

        // ... (rest of the method is unchanged)
    }
}