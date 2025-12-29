package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.Main;
import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordFieldVisible;
    @FXML private CheckBox showPasswordCheckbox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField employeeIdField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private ComboBox<String> sexComboBox;
    @FXML private ComboBox<String> shiftComboBox;
    @FXML private ComboBox<String> departmentComboBox;
    @FXML private Label errorLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        setupComboBoxes();
        setupPasswordVisibilityToggle();
    }

    private void setupPasswordVisibilityToggle() {
        passwordFieldVisible.managedProperty().bind(showPasswordCheckbox.selectedProperty());
        passwordFieldVisible.visibleProperty().bind(showPasswordCheckbox.selectedProperty());
        passwordField.managedProperty().bind(showPasswordCheckbox.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheckbox.selectedProperty().not());
        passwordFieldVisible.textProperty().bindBidirectional(passwordField.textProperty());
    }

    private void setupComboBoxes() {
        if (roleComboBox != null) {
            roleComboBox.getItems().clear();
            roleComboBox.getItems().addAll("Manager", "Employee", "Intern");
            roleComboBox.setValue("Employee");
        }
        if (sexComboBox != null) {
            sexComboBox.getItems().clear();
            sexComboBox.getItems().addAll("Male", "Female");
            sexComboBox.setValue("Male");
        }
        if (shiftComboBox != null) {
            shiftComboBox.getItems().clear();
            shiftComboBox.getItems().addAll("Day", "Night");
            shiftComboBox.setValue("Day");
        }
        if (departmentComboBox != null) {
            departmentComboBox.getItems().clear();
            departmentComboBox.getItems().addAll("HR", "IT", "Sales", "Marketing");
            departmentComboBox.setValue("IT");
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String department = departmentComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showError("Username, Password, and Email are required.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }

        if (!phone.isEmpty() && !phone.startsWith("+251")) {
            phone = "+251" + phone;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setDepartment(department);
        
        if (fullNameField != null) user.setFullName(fullNameField.getText().trim());
        if (phoneField != null) user.setPhoneNumber(phone);
        if (employeeIdField != null) user.setEmployeeId(employeeIdField.getText().trim());
        if (roleComboBox != null) user.setRole(roleComboBox.getValue());
        if (sexComboBox != null) user.setSex(sexComboBox.getValue());
        if (shiftComboBox != null) user.setShift(shiftComboBox.getValue());
        
        user.setActive(true);

        if (userDAO.createUser(user)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Successful");
            alert.setHeaderText(null);
            alert.setContentText("User registered successfully! Redirecting to login...");
            alert.showAndWait();

            handleBackToLogin();
        } else {
            showError("Registration failed. Username or Email might already exist.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        Main.switchScene("/view/login.fxml", "Instant Notification System - Login", false);
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.getStyleClass().remove("status-success");
            errorLabel.getStyleClass().add("status-failed");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.show();
        }
    }
}