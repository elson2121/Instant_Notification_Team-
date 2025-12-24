package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private VBox notificationsListContainer;
    @FXML private PieChart departmentChart;
    @FXML private LineChart<String, Number> activityChart;
    @FXML private CheckBox emailToggle;
    @FXML private CheckBox smsToggle;

    @FXML
    public void initialize() {
        welcomeLabel.setText("User"); // Placeholder

        loadRecentNotifications();
        setupCharts();
    }

    private void loadRecentNotifications() {
        if (notificationsListContainer == null) return;
        notificationsListContainer.getChildren().clear();
        
        addNotificationItem("HR Department", "Policy Update", "10 mins ago", "New");
        addNotificationItem("IT Support", "System Maintenance", "2 hours ago", "Read");
        addNotificationItem("Marketing", "New Campaign Launch", "1 day ago", "Read");
        addNotificationItem("Finance", "Payroll Processed", "2 days ago", "Read");
    }

    private void addNotificationItem(String source, String title, String time, String status) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(12));
        item.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10; -fx-border-color: rgba(255,255,255,0.1); -fx-border-radius: 10;");

        // Icon/Avatar placeholder
        StackPane iconStack = new StackPane();
        Circle icon = new Circle(20, Color.web("#3498db"));
        Label iconLabel = new Label(source.substring(0, 1));
        iconLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        iconStack.getChildren().addAll(icon, iconLabel);

        VBox content = new VBox(3);
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label sourceLabel = new Label(source + " • " + time);
        sourceLabel.setStyle("-fx-text-fill: #bdc3c7; -fx-font-size: 11px;");
        content.getChildren().addAll(titleLabel, sourceLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(status);
        String statusColor = status.equals("New") ? "#e74c3c" : "#2ecc71";
        statusLabel.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; -fx-padding: 4 10; -fx-background-radius: 15; -fx-font-size: 10px; -fx-font-weight: bold;");

        item.getChildren().addAll(iconStack, content, spacer, statusLabel);
        notificationsListContainer.getChildren().add(item);
    }

    private void setupCharts() {
        // Donut Chart (PieChart)
        if (departmentChart != null) {
            departmentChart.getData().addAll(
                new PieChart.Data("HR", 30),
                new PieChart.Data("IT", 40),
                new PieChart.Data("Marketing", 20),
                new PieChart.Data("Finance", 10)
            );
            departmentChart.setLegendVisible(true);
            departmentChart.setLabelsVisible(false);
        }

        // Line Chart
        if (activityChart != null) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Notifications");
            series.getData().add(new XYChart.Data<>("Mon", 5));
            series.getData().add(new XYChart.Data<>("Tue", 12));
            series.getData().add(new XYChart.Data<>("Wed", 8));
            series.getData().add(new XYChart.Data<>("Thu", 15));
            series.getData().add(new XYChart.Data<>("Fri", 10));
            activityChart.getData().add(series);
            activityChart.setLegendVisible(false);
        }
    }

    @FXML
    private void handleLogout() {
        SceneSwitcher.switchToLogin();
    }
}