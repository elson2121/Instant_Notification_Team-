package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.NotificationDAO;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.Notification;
import com.instantnotificationsystem.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> genderFilter;
    @FXML private ComboBox<String> shiftFilter;
    @FXML private ComboBox<String> departmentFilter;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> fullNameCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> phoneCol;
    @FXML private TableColumn<User, String> sexCol;
    @FXML private TableColumn<User, String> deptCol;
    @FXML private TableColumn<User, Void> actionsCol;

    private UserDAO userDAO;
    private NotificationDAO notificationDAO;
    private ObservableList<User> masterData = FXCollections.observableArrayList();
    private FilteredList<User> filteredData;

    public UserManagementController() {
        userDAO = new UserDAO();
        notificationDAO = new NotificationDAO();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        loadData();
    }

    private void setupTableColumns() {
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Visual Feedback for Blocked Users
        fullNameCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    User user = getTableView().getItems().get(getIndex());
                    if (!user.isActive()) {
                        setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        usernameCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (!user.isActive()) {
                        setText(item + " (BLOCKED)");
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setText(item);
                        setStyle("");
                    }
                }
            }
        });

        actionsCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btnBlock = new Button("Block");
                    private final Button btnMessage = new Button("Message");
                    private final HBox pane = new HBox(10, btnBlock, btnMessage);

                    {
                        btnBlock.getStyleClass().add("button-danger"); // Assuming you have this style
                        btnBlock.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                        btnMessage.getStyleClass().add("button-primary"); // Assuming you have this style
                        btnMessage.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
                        pane.setAlignment(Pos.CENTER);

                        btnBlock.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleBlockUser(user);
                        });

                        btnMessage.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handlePersonalMessage(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            User user = getTableView().getItems().get(getIndex());
                            if (user != null && !user.isActive()) {
                                btnBlock.setText("Unblock");
                                btnBlock.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
                            } else {
                                btnBlock.setText("Block");
                                btnBlock.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                            }
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
    }

    private void setupFilters() {
        genderFilter.setItems(FXCollections.observableArrayList("All", "Male", "Female"));
        genderFilter.getSelectionModel().selectFirst();

        shiftFilter.setItems(FXCollections.observableArrayList("All", "Morning", "Night"));
        shiftFilter.getSelectionModel().selectFirst();

        departmentFilter.setItems(FXCollections.observableArrayList("All", "HR", "IT", "Sales", "Marketing"));
        departmentFilter.getSelectionModel().selectFirst();

        // Add listeners to filters
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updatePredicate());
        genderFilter.valueProperty().addListener((observable, oldValue, newValue) -> updatePredicate());
        shiftFilter.valueProperty().addListener((observable, oldValue, newValue) -> updatePredicate());
        departmentFilter.valueProperty().addListener((observable, oldValue, newValue) -> updatePredicate());
    }

    private void loadData() {
        List<User> users = userDAO.getAllUsers();
        masterData.setAll(users);
        filteredData = new FilteredList<>(masterData, p -> true);
        usersTable.setItems(filteredData);
    }

    private void updatePredicate() {
        filteredData.setPredicate(user -> {
            String searchText = searchField.getText().toLowerCase();
            String gender = genderFilter.getValue();
            String shift = shiftFilter.getValue();
            String dept = departmentFilter.getValue();

            // Search Filter
            boolean matchesSearch = searchText.isEmpty() ||
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(searchText)) ||
                    (user.getPhoneNumber() != null && user.getPhoneNumber().contains(searchText)) ||
                    (user.getFullName() != null && user.getFullName().toLowerCase().contains(searchText));

            // ComboBox Filters
            boolean matchesGender = "All".equals(gender) || (user.getSex() != null && user.getSex().equalsIgnoreCase(gender));
            boolean matchesShift = "All".equals(shift) || (user.getShift() != null && user.getShift().equalsIgnoreCase(shift));
            boolean matchesDept = "All".equals(dept) || (user.getDepartment() != null && user.getDepartment().equalsIgnoreCase(dept));

            return matchesSearch && matchesGender && matchesShift && matchesDept;
        });
    }

    private void handleBlockUser(User user) {
        boolean newStatus = !user.isActive();
        String action = newStatus ? "unblock" : "block";
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Action");
        alert.setHeaderText("Are you sure you want to " + action + " " + user.getUsername() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userDAO.updateUserStatus(user.getId(), newStatus)) {
                user.setActive(newStatus);
                usersTable.refresh();
                new Alert(Alert.AlertType.INFORMATION, "User " + action + "ed successfully.").showAndWait();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update user status.").showAndWait();
            }
        }
    }

    private void handlePersonalMessage(User user) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send Personal Message");
        dialog.setHeaderText("Send a message to " + user.getFullName());
        dialog.setContentText("Message:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(message -> {
            if (!message.trim().isEmpty()) {
                Notification notification = new Notification();
                notification.setTitle("Personal Message from Admin");
                notification.setMessage(message);
                notification.setSenderId(1); // Admin ID
                notification.setNotificationType("Personal");
                notification.setChannels(new ArrayList<>(List.of("DASHBOARD"))); // Default channel

                int notificationId = notificationDAO.createNotification(notification);
                if (notificationId > 0) {
                    List<User> target = new ArrayList<>();
                    target.add(user);
                    notificationDAO.createUserNotifications(notificationId, target);
                    new Alert(Alert.AlertType.INFORMATION, "Message sent successfully!").showAndWait();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to send message.").showAndWait();
                }
            }
        });
    }
}