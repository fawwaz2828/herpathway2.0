package com.herpathway.controller;

import com.herpathway.dao.ForumDao;
import com.herpathway.model.ForumThread;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;

/** Form for starting a new forum discussion. */
public class ForumComposeController {

    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_BODY_LENGTH = 10_000;

    @FXML
    private ComboBox<String> categoryBox;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea bodyArea;
    @FXML
    private Label errorLabel;

    private final ForumDao forumDao = new ForumDao();

    @FXML
    public void initialize() {
        categoryBox.getItems().setAll(ForumThread.CATEGORIES);
    }

    @FXML
    private void handlePublish() {
        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String body = bodyArea.getText() == null ? "" : bodyArea.getText().trim();
        if (categoryBox.getValue() == null) {
            showError("Choose a category.");
            return;
        }
        if (title.length() < 8) {
            showError("Write a title of at least 8 characters.");
            return;
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            showError("The title can be at most " + MAX_TITLE_LENGTH + " characters.");
            return;
        }
        if (body.length() < 20) {
            showError("Add some details (at least 20 characters) so others can help.");
            return;
        }
        if (body.length() > MAX_BODY_LENGTH) {
            showError("The details can be at most " + MAX_BODY_LENGTH + " characters.");
            return;
        }
        long id;
        try {
            id = forumDao.createThread(SessionManager.getInstance().getCurrentUserId(), categoryBox.getValue(), title, body);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Couldn't publish: " + e.getMessage());
            return;
        }
        ForumController.open(id);
    }

    @FXML
    private void handleCancel() {
        Navigator.show(View.FORUM);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        Ui.setShown(errorLabel, true);
    }
}
