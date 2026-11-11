package com.herpathway.controller;

import com.herpathway.dao.LearningDao;
import com.herpathway.model.LearningItem;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.sql.SQLException;

/** Form for publishing to the learning library. */
public class ContentEditorController {

    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_SUMMARY_LENGTH = 500;
    private static final int MAX_URL_LENGTH = 500;

    @FXML
    private HBox typeChips;
    @FXML
    private TextField titleField;
    @FXML
    private TextField summaryField;
    @FXML
    private VBox bodyBox;
    @FXML
    private TextArea bodyArea;
    @FXML
    private VBox urlBox;
    @FXML
    private TextField urlField;
    @FXML
    private Label errorLabel;

    private final LearningDao learningDao = new LearningDao();
    private final ToggleGroup types = new ToggleGroup();
    private String type = LearningItem.ARTICLE;

    @FXML
    public void initialize() {
        for (String t : new String[]{LearningItem.ARTICLE, LearningItem.VIDEO, LearningItem.BOOK}) {
            ToggleButton chip = new ToggleButton(LearningItem.labelOf(t));
            chip.getStyleClass().add("chip");
            chip.setUserData(t);
            chip.setToggleGroup(types);
            chip.setSelected(t.equals(type));
            typeChips.getChildren().add(chip);
        }
        types.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                old.setSelected(true);
                return;
            }
            type = (String) selected.getUserData();
            showFieldsForType();
        });
        showFieldsForType();
    }

    private void showFieldsForType() {
        boolean article = LearningItem.ARTICLE.equals(type);
        Ui.setShown(bodyBox, article);
        Ui.setShown(urlBox, !article);
    }

    @FXML
    private void handlePublish() {
        if (!ContentController.canPublish()) {
            showError("Only mentors and organizations can publish content.");
            return;
        }
        String title = text(titleField.getText());
        String summary = text(summaryField.getText());
        String body = text(bodyArea.getText());
        String url = text(urlField.getText());
        boolean article = LearningItem.ARTICLE.equals(type);

        if (title.length() < 5 || title.length() > MAX_TITLE_LENGTH) {
            showError("Write a title between 5 and " + MAX_TITLE_LENGTH + " characters.");
            return;
        }
        if (summary.length() < 10 || summary.length() > MAX_SUMMARY_LENGTH) {
            showError("Write a summary between 10 and " + MAX_SUMMARY_LENGTH + " characters.");
            return;
        }
        if (article && body.length() < 100) {
            showError("The article is too short. Write at least 100 characters.");
            return;
        }
        if (!article && !isWebLink(url)) {
            showError("Enter a full link starting with http:// or https://");
            return;
        }

        long id;
        try {
            id = learningDao.insert(SessionManager.getInstance().getCurrentUserId(), type, title, summary,
                    article ? body : null, article ? null : url);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Couldn't publish: " + e.getMessage());
            return;
        }
        ContentController.open(id);
    }

    private static boolean isWebLink(String url) {
        if (url.length() > MAX_URL_LENGTH) {
            return false;
        }
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            return uri.getHost() != null && ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme));
        } catch (Exception e) {
            return false;
        }
    }

    private static String text(String s) {
        return s == null ? "" : s.trim();
    }

    @FXML
    private void handleCancel() {
        Navigator.show(View.CONTENT);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        Ui.setShown(errorLabel, true);
    }
}
