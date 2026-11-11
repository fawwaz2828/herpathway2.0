package com.herpathway.controller;

import com.herpathway.dao.UserDao;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterController {

    private static final Pattern EMAIL =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String[] USER_TYPES = {UserDao.MENTEE, UserDao.MENTOR, UserDao.ORGANIZATION};

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
    @FXML
    private Label errorLabel;

    private final UserDao userDao = new UserDao();

    @FXML
    public void initialize() {
        userTypeComboBox.getItems().addAll(USER_TYPES);
        userTypeComboBox.setValue(USER_TYPES[0]);
        // Show "Mentee" instead of the raw database value "mentee".
        userTypeComboBox.setButtonCell(new RoleCell());
        userTypeComboBox.setCellFactory(list -> new RoleCell());
    }

    @FXML
    protected void handleShowPasswordAction(ActionEvent event) {
        boolean show = showPasswordToggle.isSelected();
        if (show) {
            passwordTextField.setText(passwordField.getText());
            confirmPasswordTextField.setText(confirmPasswordField.getText());
        } else {
            passwordField.setText(passwordTextField.getText());
            confirmPasswordField.setText(confirmPasswordTextField.getText());
        }
        setShown(passwordTextField, show);
        setShown(confirmPasswordTextField, show);
        setShown(passwordField, !show);
        setShown(confirmPasswordField, !show);
    }

    @FXML
    protected void handleRegisterButtonAction(ActionEvent event) {
        boolean show = showPasswordToggle.isSelected();
        String username = usernameField.getText().trim();
        String password = show ? passwordTextField.getText() : passwordField.getText();
        String confirmPassword = show ? confirmPasswordTextField.getText() : confirmPasswordField.getText();
        String email = emailField.getText().trim();

        if (username.isEmpty()) {
            showError("Choose a username.");
            return;
        }
        if (!EMAIL.matcher(email).matches()) {
            showError("Enter a valid email address.");
            return;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            showError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError("Passwords don't match.");
            return;
        }

        try {
            if (userDao.usernameExists(username)) {
                showError("That username is already taken.");
                return;
            }
            if (userDao.emailExists(email)) {
                showError("An account with that email already exists.");
                return;
            }
            userDao.insert(username, password, email, userTypeComboBox.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Couldn't create the account: " + e.getMessage());
            return;
        }
        Navigator.<LoginController>goTo(View.LOGIN, c -> c.prefill(username));
    }

    @FXML
    protected void handleBackToLoginLinkAction(ActionEvent event) {
        Navigator.goTo(View.LOGIN);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    private static void setShown(TextField field, boolean shown) {
        field.setVisible(shown);
        field.setManaged(shown);
    }

    private static final class RoleCell extends ListCell<String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : Character.toUpperCase(item.charAt(0)) + item.substring(1));
        }
    }
}
