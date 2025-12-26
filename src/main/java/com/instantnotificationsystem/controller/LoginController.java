package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.service.SessionManager;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginPasswordFieldVisible;
    @FXML private CheckBox showLoginPasswordCheckbox;
    @FXML private Label loginErrorLabel;
    @FXML private VBox loginForm;
    @FXML private VBox registerForm;
    @FXML private VBox regStep1;
    @FXML private VBox regStep2;
    @FXML private Label regStepLabel;
    @FXML private TextField regFullNameField;
    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private TextField regPasswordFieldVisible;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private TextField regConfirmPasswordFieldVisible;
    @FXML private CheckBox showRegPasswordCheckbox;
    @FXML private TextField regPhoneField;
    @FXML private TextField regEmployeeIdField;
    @FXML private ComboBox<String> regRoleComboBox;
    @FXML private ComboBox<String> regSexComboBox;
    @FXML private ComboBox<String> regShiftComboBox;
    @FXML private ComboBox<String> regDepartmentComboBox;
    @FXML private Label regErrorLabel;

    private UserDAO userDAO;

    public void initialize() {
        userDAO = new UserDAO();
        setupPasswordVisibility();
        setupComboBoxes();
    }

    private void setupPasswordVisibility() {
        showLoginPasswordCheckbox.selectedProperty().addListener((obs, was, isNow) -> {
            loginPasswordFieldVisible.setText(loginPasswordField.getText());
            loginPasswordFieldVisible.setVisible(isNow);
            loginPasswordField.setVisible(!isNow);
        });
        loginPasswordField.textProperty().bindBidirectional(loginPasswordFieldVisible.textProperty());

        showRegPasswordCheckbox.selectedProperty().addListener((obs, was, isNow) -> {
            regPasswordFieldVisible.setText(regPasswordField.getText());
            regPasswordFieldVisible.setVisible(isNow);
            regPasswordField.setVisible(!isNow);
            regConfirmPasswordFieldVisible.setText(regConfirmPasswordField.getText());
            regConfirmPasswordFieldVisible.setVisible(isNow);
            regConfirmPasswordField.setVisible(!isNow);
        });
        regPasswordField.textProperty().bindBidirectional(regPasswordFieldVisible.textProperty());
        regConfirmPasswordField.textProperty().bindBidirectional(regConfirmPasswordFieldVisible.textProperty());
    }

    private void setupComboBoxes() {
        regRoleComboBox.getItems().addAll("User", "Admin");
        regSexComboBox.getItems().addAll("Male", "Female", "Other");
        regShiftComboBox.getItems().addAll("Day Shift", "Night Shift", "Flexible");
        regDepartmentComboBox.getItems().addAll("IT", "HR", "Finance", "Operations", "Sales");
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.isVisible() ? loginPasswordField.getText() : loginPasswordFieldVisible.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password", true);
            return;
        }

        // Hardcoded Admin Login - Updated password to "admin1"
        if ("admin".equals(username) && "admin1".equals(password)) {
            User adminUser = new User();
            adminUser.setId(1); // Assuming Admin ID is 1
            adminUser.setFullName("Administrator");
            adminUser.setUsername("admin");
            adminUser.setRole("Admin");
            SessionManager.setUser(adminUser);
            loadDashboard(adminUser);
            return;
        }

        User user = userDAO.getUserByUsernameAndPassword(username, password);

        if (user != null) {
            SessionManager.setUser(user);
            loadDashboard(user);
        } else {
            showError("Invalid username or password", true);
        }
    }

    @FXML
    private void handleCompleteRegistration() {
        if (regEmployeeIdField.getText().isEmpty() || regRoleComboBox.getValue() == null || regSexComboBox.getValue() == null || regShiftComboBox.getValue() == null || regDepartmentComboBox.getValue() == null) {
            showError("Please fill in all fields", false);
            return;
        }

        String selectedShift = regShiftComboBox.getValue();
        String finalShiftValue;

        switch (selectedShift) {
            case "Day Shift":
                finalShiftValue = "Day";
                break;
            case "Night Shift":
                finalShiftValue = "Night";
                break;
            case "Flexible":
                finalShiftValue = "Flexible";
                break;
            default:
                showError("Invalid shift selected. Please choose from the list.", false);
                return;
        }

        System.out.println("Final Shift Value: " + finalShiftValue);

        User newUser = new User();
        newUser.setFullName(regFullNameField.getText());
        newUser.setUsername(regUsernameField.getText());
        newUser.setPassword(regPasswordField.getText());
        newUser.setPhoneNumber(regPhoneField.getText());
        newUser.setEmployeeId(regEmployeeIdField.getText());
        newUser.setRole(regRoleComboBox.getValue());
        newUser.setSex(regSexComboBox.getValue());
        newUser.setShift(finalShiftValue);
        newUser.setDepartmentName(regDepartmentComboBox.getValue());

        try {
            if (userDAO.createUser(newUser)) {
                showLoginView();
                loginErrorLabel.setText("Registration successful! Please sign in.");
                loginErrorLabel.setStyle("-fx-text-fill: green;");
                loginErrorLabel.setVisible(true);
            } else {
                showError("Registration failed. Username or Employee ID might already exist.", false);
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage(), false);
        }
    }

    // Other methods remain unchanged
    @FXML
    private void handleRegister() {
        loginForm.setVisible(false);
        registerForm.setVisible(true);
        regStep1.setVisible(true);
        regStep2.setVisible(false);
        regStepLabel.setText("Step 1 of 2");
        clearRegistrationFields();
    }

    @FXML
    private void showLoginView() {
        registerForm.setVisible(false);
        loginForm.setVisible(true);
        clearLoginFields();
    }

    @FXML
    private void handleNextStep() {
        if (regFullNameField.getText().isEmpty() || regUsernameField.getText().isEmpty() || regPasswordField.getText().isEmpty() || regPhoneField.getText().isEmpty()) {
            showError("Please fill in all fields", false);
            return;
        }
        if (!regPasswordField.getText().equals(regConfirmPasswordField.getText())) {
            showError("Passwords do not match", false);
            return;
        }
        regStep1.setVisible(false);
        regStep2.setVisible(true);
        regStepLabel.setText("Step 2 of 2");
        regErrorLabel.setVisible(false);
    }

    @FXML
    private void handlePrevStep() {
        regStep2.setVisible(false);
        regStep1.setVisible(true);
        regStepLabel.setText("Step 1 of 2");
        regErrorLabel.setVisible(false);
    }

    private void loadDashboard(User user) {
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                SceneSwitcher.switchToAdminDashboard(stage, user.getFullName(), user.getId());
            } else {
                SceneSwitcher.switchToDashboard(stage, user.getFullName(), user.getId());
            }
        } catch (IOException ex) {
            showError("Error loading dashboard: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    private void showError(String message, boolean isLoginError) {
        Label targetLabel = isLoginError ? loginErrorLabel : regErrorLabel;
        targetLabel.setText(message);
        targetLabel.setStyle("-fx-text-fill: #ef4444;");
        targetLabel.setVisible(true);
    }

    private void clearLoginFields() {
        loginUsernameField.clear();
        loginPasswordField.clear();
        loginPasswordFieldVisible.clear();
        loginErrorLabel.setVisible(false);
    }

    private void clearRegistrationFields() {
        regFullNameField.clear();
        regUsernameField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
        regPhoneField.clear();
        regEmployeeIdField.clear();
        regRoleComboBox.getSelectionModel().clearSelection();
        regSexComboBox.getSelectionModel().clearSelection();
        regShiftComboBox.getSelectionModel().clearSelection();
        regDepartmentComboBox.getSelectionModel().clearSelection();
        regErrorLabel.setVisible(false);
    }
}