package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
    private NotificationDAO notificationDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        notificationDAO = new NotificationDAO();

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
        
        // Get real counts from DB
        int totalUsers = userDAO.getTotalUserCount();
        int sentNotifications = notificationDAO.getSentCount();

        // Stat cards with full color background and icons
        addStatCard(statsGrid, "Total Users", String.valueOf(totalUsers), 0, 0, "#2ecc71", "👥"); // Green
        addStatCard(statsGrid, "Notifications Sent", String.valueOf(sentNotifications), 0, 1, "#3498db", "✉️"); // Blue

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
        card.getStyleClass().add("stat-card");
        card.setStyle("-fx-background-color: " + colorHex + "; -fx-background-radius: 12px;");
        card.setPrefWidth(400);
        card.setPrefHeight(120);

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
        VBox formBox = new VBox(20);
        formBox.setPadding(new Insets(30));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        formBox.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("Send Notification");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        VBox inputContainer = new VBox(15);
        inputContainer.setMaxWidth(600);
        inputContainer.setAlignment(Pos.CENTER_LEFT);

        TextField titleField = new TextField();
        titleField.setPromptText("Enter Subject...");
        titleField.setPrefHeight(40);
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type message here...");
        messageArea.setPrefRowCount(5);
        
        ComboBox<String> sendToCombo = new ComboBox<>();
        sendToCombo.getItems().addAll("All Users", "Specific Department", "Specific User");
        sendToCombo.setValue("All Users");
        sendToCombo.setMaxWidth(Double.MAX_VALUE);
        sendToCombo.setPrefHeight(40);

        CheckBox emailCheck = new CheckBox("Email");
        emailCheck.setId("emailToggle");
        
        CheckBox smsCheck = new CheckBox("SMS");
        smsCheck.setId("smsToggle");
        
        HBox channelBox = new HBox(20, new Label("Delivery Method:"), emailCheck, smsCheck);
        channelBox.setAlignment(Pos.CENTER_LEFT);
        
        inputContainer.getChildren().addAll(
            new VBox(5, new Label("Subject"), titleField),
            new VBox(5, new Label("Message"), messageArea),
            new VBox(5, new Label("Send To"), sendToCombo),
            channelBox
        );
        
        Button sendBtn = new Button("Send Notification");
        sendBtn.setStyle("-fx-background-color: #2D62ED; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 8; -fx-cursor: hand;");
        sendBtn.setPrefWidth(200);
        sendBtn.setPrefHeight(45);
        sendBtn.setDisable(true); // Disabled by default

        // Validation Logic
        Runnable validate = () -> {
            boolean titleOk = !titleField.getText().trim().isEmpty();
            boolean messageOk = !messageArea.getText().trim().isEmpty();
            boolean channelOk = emailCheck.isSelected() || smsCheck.isSelected();
            sendBtn.setDisable(!(titleOk && messageOk && channelOk));
        };

        titleField.textProperty().addListener((obs, old, aNew) -> validate.run());
        messageArea.textProperty().addListener((obs, old, aNew) -> validate.run());
        emailCheck.selectedProperty().addListener((obs, old, aNew) -> validate.run());
        smsCheck.selectedProperty().addListener((obs, old, aNew) -> validate.run());

        // Send and Clear Logic
        sendBtn.setOnAction(e -> {
            System.out.println("Sending notification...");
            System.out.println("Title: " + titleField.getText());
            System.out.println("Message: " + messageArea.getText());
            List<String> channels = new ArrayList<>();
            if (emailCheck.isSelected()) channels.add("Email");
            if (smsCheck.isSelected()) channels.add("SMS");
            System.out.println("Channels: " + String.join(", ", channels));

            // --- Actual sending logic would go here ---

            // Clear form after sending
            titleField.clear();
            messageArea.clear();
            emailCheck.setSelected(false);
            smsCheck.setSelected(false);
            sendToCombo.setValue("All Users");
            
            System.out.println("Form cleared.");
        });
        
        formBox.getChildren().addAll(titleLabel, inputContainer, sendBtn);
        return formBox;
    }
    
    private VBox createRecentNotificationsBox() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; -fx-background-radius: 12px;");
        box.getStyleClass().add("table-view-container");

        Label titleLabel = new Label("Recent Notifications");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        TableView<NotificationMock> table = new TableView<>();
        table.setPrefHeight(300);
        table.getStyleClass().add("table-view");
        
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
                    switch (item) {
                        case "Delivered":
                            setTextFill(Color.GREEN);
                            break;
                        case "Sent":
                            setTextFill(Color.BLUE);
                            break;
                        case "Failed":
                            setTextFill(Color.RED);
                            break;
                        default:
                            setTextFill(Color.BLACK);
                            break;
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