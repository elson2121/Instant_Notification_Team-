package com.instantnotificationsystem.controller;

import com.instantnotificationsystem.dao.UserDAO;
import com.instantnotificationsystem.model.User;
import com.instantnotificationsystem.utils.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    // Login Fields
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField loginPasswordFieldVisible;
    @FXML private CheckBox showLoginPasswordCheckbox;
    @FXML private Label loginErrorLabel;
    @FXML private VBox loginForm;

    // Registration Fields
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
        // Login password visibility
        if (showLoginPasswordCheckbox != null) {
            showLoginPasswordCheckbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    loginPasswordFieldVisible.setText(loginPasswordField.getText());
                    loginPasswordFieldVisible.setVisible(true);
                    loginPasswordField.setVisible(false);
                } else {
                    loginPasswordField.setText(loginPasswordFieldVisible.getText());
                    loginPasswordFieldVisible.setVisible(false);
                    loginPasswordField.setVisible(true);
                }
            });
            
            // Sync text between fields
            loginPasswordField.textProperty().bindBidirectional(loginPasswordFieldVisible.textProperty());
        }

        // Registration password visibility
        if (showRegPasswordCheckbox != null) {
            showRegPasswordCheckbox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    regPasswordFieldVisible.setText(regPasswordField.getText());
                    regPasswordFieldVisible.setVisible(true);
                    regPasswordField.setVisible(false);
                    
                    regConfirmPasswordFieldVisible.setText(regConfirmPasswordField.getText());
                    regConfirmPasswordFieldVisible.setVisible(true);
                    regConfirmPasswordField.setVisible(false);
                } else {
                    regPasswordField.setText(regPasswordFieldVisible.getText());
                    regPasswordFieldVisible.setVisible(false);
                    regPasswordField.setVisible(true);
                    
                    regConfirmPasswordField.setText(regConfirmPasswordFieldVisible.getText());
                    regConfirmPasswordFieldVisible.setVisible(false);
                    regConfirmPasswordField.setVisible(true);
                }
            });
            
            // Sync text between fields
            regPasswordField.textProperty().bindBidirectional(regPasswordFieldVisible.textProperty());
            regConfirmPasswordField.textProperty().bindBidirectional(regConfirmPasswordFieldVisible.textProperty());
        }
    }

    private void setupComboBoxes() {
        if (regRoleComboBox != null) {
            regRoleComboBox.getItems().addAll("User", "Admin");
        }
        if (regSexComboBox != null) {
            regSexComboBox.getItems().addAll("Male", "Female", "Other");
        }
        if (regShiftComboBox != null) {
            regShiftComboBox.getItems().addAll("Morning", "Afternoon", "Night");
        }
        if (regDepartmentComboBox != null) {
            regDepartmentComboBox.getItems().addAll("IT", "HR", "Finance", "Operations", "Sales");
        }
    }

    @FXML
    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.isVisible() ? loginPasswordField.getText() : loginPasswordFieldVisible.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password", true);
            return;
        }

        // Hardcoded Admin Login
        if ("admin".equals(username) && "admin123".equals(password)) {
            User adminUser = new User();
            adminUser.setFullName("Administrator");
            adminUser.setUsername("admin");
            adminUser.setRole("Admin");
            adminUser.setId(0); // Special ID for hardcoded admin
            loadDashboard(adminUser);
            return;
        }

        User user = userDAO.getUserByUsernameAndPassword(username, password);

        if (user != null) {
            loadDashboard(user);
        } else {
            showError("Invalid username or password", true);
        }
    }

    @FXML
    private void handleRegister() {
        // Switch to registration view (hide login form, show register form)
        loginForm.setVisible(false);
        registerForm.setVisible(true);
        
        // Reset registration form
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
        // Validate Step 1
        if (regFullNameField.getText().isEmpty() || 
            regUsernameField.getText().isEmpty() || 
            (regPasswordField.isVisible() ? regPasswordField.getText() : regPasswordFieldVisible.getText()).isEmpty() ||
            regPhoneField.getText().isEmpty()) {
            showError("Please fill in all fields", false);
            return;
        }

        String pass = regPasswordField.isVisible() ? regPasswordField.getText() : regPasswordFieldVisible.getText();
        String confirmPass = regConfirmPasswordField.isVisible() ? regConfirmPasswordField.getText() : regConfirmPasswordFieldVisible.getText();

        if (!pass.equals(confirmPass)) {
            showError("Passwords do not match", false);
            return;
        }

        // Move to Step 2
        regStep1.setVisible(false);
        regStep2.setVisible(true);
        regStepLabel.setText("Step 2 of 2");
        if (regErrorLabel != null) regErrorLabel.setVisible(false);
    }

    @FXML
    private void handlePrevStep() {
        regStep2.setVisible(false);
        regStep1.setVisible(true);
        regStepLabel.setText("Step 1 of 2");
        if (regErrorLabel != null) regErrorLabel.setVisible(false);
    }

    @FXML
    private void handleCompleteRegistration() {
        // Validate Step 2
        if (regEmployeeIdField.getText().isEmpty() || 
            regRoleComboBox.getValue() == null || 
            regSexComboBox.getValue() == null || 
            regShiftComboBox.getValue() == null || 
            regDepartmentComboBox.getValue() == null) {
            showError("Please fill in all fields", false);
            return;
        }

        // Create new user object
        User newUser = new User();
        newUser.setFullName(regFullNameField.getText());
        newUser.setUsername(regUsernameField.getText());
        newUser.setPassword(regPasswordField.isVisible() ? regPasswordField.getText() : regPasswordFieldVisible.getText());
        newUser.setPhoneNumber(regPhoneField.getText());
        newUser.setEmployeeId(regEmployeeIdField.getText());
        newUser.setRole(regRoleComboBox.getValue());
        newUser.setSex(regSexComboBox.getValue());
        newUser.setShift(regShiftComboBox.getValue());
        newUser.setDepartmentName(regDepartmentComboBox.getValue());

        // Save to database
        boolean success = userDAO.createUser(newUser);

        if (success) {
            showLoginView();
            // Ideally show a success message
            if (loginErrorLabel != null) {
                loginErrorLabel.setText("Registration successful! Please sign in.");
                loginErrorLabel.setStyle("-fx-text-fill: green;");
                loginErrorLabel.setVisible(true);
            }
        } else {
            showError("Registration failed. Username or Employee ID might already exist.", false);
        }
    }

    private void loadDashboard(User user) {
        try {
            Stage stage = (Stage) loginUsernameField.getScene().getWindow();
            String userFullName = user.getFullName();
            int userId = user.getId();
            
            // Determine which dashboard to load based on role
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                SceneSwitcher.switchToAdminDashboard(stage, userFullName, userId);
            } else {
                SceneSwitcher.switchToDashboard(stage, userFullName, userId);
            }

        } catch (IOException ex) {
            showError("Error loading dashboard: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    private void showError(String message, boolean isLoginError) {
        Label targetLabel = isLoginError ? loginErrorLabel : regErrorLabel;
        if (targetLabel != null) {
            targetLabel.setText(message);
            targetLabel.setStyle("-fx-text-fill: #ef4444;");
            targetLabel.setVisible(true);
        }
    }

    private void clearLoginFields() {
        if (loginUsernameField != null) loginUsernameField.clear();
        if (loginPasswordField != null) loginPasswordField.clear();
        if (loginPasswordFieldVisible != null) loginPasswordFieldVisible.clear();
        if (loginErrorLabel != null) loginErrorLabel.setVisible(false);
    }

    private void clearRegistrationFields() {
        if (regFullNameField != null) regFullNameField.clear();
        if (regUsernameField != null) regUsernameField.clear();
        if (regPasswordField != null) regPasswordField.clear();
        if (regPasswordFieldVisible != null) regPasswordFieldVisible.clear();
        if (regConfirmPasswordField != null) regConfirmPasswordField.clear();
        if (regConfirmPasswordFieldVisible != null) regConfirmPasswordFieldVisible.clear();
        if (regPhoneField != null) regPhoneField.clear();
        if (regEmployeeIdField != null) regEmployeeIdField.clear();
        if (regRoleComboBox != null) regRoleComboBox.getSelectionModel().clearSelection();
        if (regSexComboBox != null) regSexComboBox.getSelectionModel().clearSelection();
        if (regShiftComboBox != null) regShiftComboBox.getSelectionModel().clearSelection();
        if (regDepartmentComboBox != null) regDepartmentComboBox.getSelectionModel().clearSelection();
        if (regErrorLabel != null) regErrorLabel.setVisible(false);
    }
}