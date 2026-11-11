package com.herpathway.controller;

import com.herpathway.dao.UserDao;
import com.herpathway.model.User;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

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
    private Label errorLabel;

    private final UserDao userDao = new UserDao();

    @FXML
    public void initialize() {
        // The plain-text field and the password field share one value; the checkbox picks which is visible.
        passwordTextField.managedProperty().bind(showPasswordToggle.selectedProperty());
        passwordTextField.visibleProperty().bind(showPasswordToggle.selectedProperty());
        passwordField.managedProperty().bind(showPasswordToggle.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordToggle.selectedProperty().not());
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    /** Called after registration: fill in the new username and show a confirmation. */
    public void prefill(String username) {
        usernameField.setText(username);
        showMessage("Account created. Log in to set up your profile.", false);
        passwordField.requestFocus();
    }

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Enter your username and password.", true);
            return;
        }

        try {
            User user = userDao.authenticate(username, password);
            if (user == null) {
                showMessage("Incorrect username or password.", true);
                return;
            }
            View destination = destinationFor(user);
            if (destination == null) {
                showMessage("This account has an unknown role.", true);
                return;
            }
            SessionManager.getInstance().login(user);
            Navigator.goTo(destination);
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Can't reach the database. Is MySQL running? (" + e.getMessage() + ")", true);
        }
    }

    /** Users who haven't filled in their profile go to the onboarding form for their role first. */
    private View destinationFor(User user) throws SQLException {
        boolean complete = userDao.hasCompletedProfile(user.getId(), user.getUserType());
        switch (user.getUserType()) {
            case UserDao.MENTEE:
                return complete ? View.HOME : View.MENTEE_DATA_FORM;
            case UserDao.ORGANIZATION:
                return complete ? View.HOME : View.ORGANIZATION_DATA_FORM;
            case UserDao.MENTOR:
                return complete ? View.HOME : View.MENTOR_DATA_FORM;
            default:
                return null;
        }
    }

    @FXML
    protected void handleRegisterLinkAction(ActionEvent event) {
        Navigator.goTo(View.REGISTER);
    }

    private void showMessage(String text, boolean isError) {
        errorLabel.setText(text);
        errorLabel.getStyleClass().removeAll("error-label", "success-label");
        errorLabel.getStyleClass().add(isError ? "error-label" : "success-label");
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
