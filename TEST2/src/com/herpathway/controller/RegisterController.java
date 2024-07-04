package com.herpathway.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.herpathway.database.UserDatabase;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    private CheckBox showPasswordToggle;

    @FXML
    private ComboBox<String> userTypeComboBox;

    private String[] userType = {"mentee", "mentor", "organization"};

    @FXML
    protected void handleShowPasswordAction(ActionEvent event) {
        boolean showPassword = showPasswordToggle.isSelected();
        passwordTextField.setText(passwordField.getText());
        confirmPasswordTextField.setText(confirmPasswordField.getText());

        passwordTextField.setVisible(showPassword);
        passwordTextField.setManaged(showPassword);
        confirmPasswordTextField.setVisible(showPassword);
        confirmPasswordTextField.setManaged(showPassword);

        passwordField.setVisible(!showPassword);
        passwordField.setManaged(!showPassword);
        confirmPasswordField.setVisible(!showPassword);
        confirmPasswordField.setManaged(!showPassword);
    }

    @FXML
    protected void handleRegisterButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = showPasswordToggle.isSelected() ? passwordTextField.getText() : passwordField.getText();
        String confirmPassword = showPasswordToggle.isSelected() ? confirmPasswordTextField.getText() : confirmPasswordField.getText();
        String email = emailField.getText();
        String userType = userTypeComboBox.getValue();

        // Validasi email
        if (!isValidEmail(email)) {
            showAlert("Registration Failed", "Invalid email format. Please enter a valid email address.");
            return;
        }

        // Validasi password
        if (password.length() < 8) {
            showAlert("Registration Failed", "Password must be at least 8 characters long.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.");
            return;
        }

        try {
            if (UserDatabase.userExists(username)) {
                showAlert("Registration Failed", "Username already exists.");
            } else {
                UserDatabase.addUser(username, password, email, userType);
                showAlert("Registration Successful", "User registered successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while accessing the user database: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        initializeComboBox(userTypeComboBox);
    }

    private void initializeComboBox(ComboBox<String> comboBox) {
        comboBox.getItems().addAll(userType);
        comboBox.setValue(userType[0]);
    }

    @FXML
    protected void handleBackToLoginLinkAction(ActionEvent event) {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/herpathway/view/LoginView.fxml"));
            stage.setScene(new Scene(root, 300, 200));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the login page: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

