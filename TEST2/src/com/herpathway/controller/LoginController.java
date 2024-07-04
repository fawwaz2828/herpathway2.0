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

import com.herpathway.database.UserDatabase;
import com.herpathway.session.SessionManager;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private CheckBox showPasswordToggle;

    @FXML
    public void initialize() {
        // Bind passwordField and passwordTextField
        passwordTextField.managedProperty().bind(showPasswordToggle.selectedProperty());
        passwordTextField.visibleProperty().bind(showPasswordToggle.selectedProperty());
        passwordField.managedProperty().bind(showPasswordToggle.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordToggle.selectedProperty().not());
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = showPasswordToggle.isSelected() ? passwordTextField.getText() : passwordField.getText();

        try {
            long userId = UserDatabase.validateUser(username, password);
            if (userId != -1) {
                String userType = UserDatabase.getUserType(username);
        
                // Store the logged-in user's information in SessionManager
                SessionManager.getInstance().setCurrentUser(userId, username);
        
                boolean isComplete = UserDatabase.isUserDataComplete(userId, userType);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader;
                Parent root;
        
                if (isComplete) {
                    switch (userType) {
                        case "mentee":
                        case "organization":
                            loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/Homepage.fxml"));
                            root = loader.load();
                            break;
                        case "mentor":
                            loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/MentorHomepage.fxml"));
                            root = loader.load();
                            break;
                        default:
                            showAlert("Error", "User type not recognized.");
                            return;
                    }
                } else {
                    switch (userType) {
                        case "mentee":
                            loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/MenteeDataForm.fxml"));
                            root = loader.load();
                            break;
                        case "mentor":
                            loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/MentorDataForm.fxml"));
                            root = loader.load();
                            break;
                        case "organization":
                            loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/OrganizationDataForm.fxml"));
                            root = loader.load();
                            break;
                        default:
                            showAlert("Error", "User type not recognized.");
                            return;
                    }
                }
        
                // Set HomepageController or MentorHomepageController as UserData
                Object controller = loader.getController();
                stage.setUserData(controller);
        
                stage.setScene(new Scene(root, 967, 677));
                stage.show();
                showAlert("Login Successful", "Welcome, " + username + "!");
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }        

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void handleRegisterLinkAction(ActionEvent event) {
        // Logic to navigate to the registration page
        Stage stage = (Stage) usernameField.getScene().getWindow();
        Parent root;

        try {
            root = FXMLLoader.load(getClass().getResource("/com/herpathway/view/RegisterView.fxml"));
            stage.setScene(new Scene(root, 400, 300));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading the registration form: " + e.getMessage());
        }
    }
}
