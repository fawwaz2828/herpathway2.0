package com.herpathway.controller;

import com.herpathway.dao.LearningDao;
import com.herpathway.model.LearningItem;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.net.URI;
import java.sql.SQLException;

/** Shows one learning item: reads an article in place, or links out to a video / e-book. */
public class ContentDetailController {

    @FXML
    private Label typeBadge;
    @FXML
    private Label metaLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label summaryLabel;
    @FXML
    private VBox articleBody;
    @FXML
    private VBox linkBox;
    @FXML
    private Label urlLabel;
    @FXML
    private Button copyButton;

    private final LearningDao learningDao = new LearningDao();
    private LearningItem item;

    public void setItem(long itemId) {
        try {
            item = learningDao.findById(itemId);
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        if (item == null) {
            Alerts.info("Content removed", "This content is no longer available.");
            Navigator.show(View.CONTENT);
            return;
        }

        typeBadge.setText(item.typeLabel());
        String variant = ContentController.badgeVariant(item.getType());
        if (variant != null) {
            typeBadge.getStyleClass().add(variant);
        }
        String meta = "By " + item.getAuthorName() + "  ·  " + Ui.timeAgo(item.getCreatedAt());
        metaLabel.setText(item.isArticle() ? meta + "  ·  " + item.readingMinutes() + " min read" : meta);
        titleLabel.setText(item.getTitle());
        summaryLabel.setText(item.getSummary());
        Ui.setShown(summaryLabel, !Ui.isBlank(item.getSummary()));
        Ui.setShown(deleteButton, item.getAuthorId() == SessionManager.getInstance().getCurrentUserId());

        Ui.setShown(articleBody, item.isArticle());
        Ui.setShown(linkBox, !item.isArticle());
        if (item.isArticle()) {
            renderArticle(item.getBody());
        } else {
            urlLabel.setText(item.getUrl());
        }
    }

    /**
     * Paragraphs are separated by blank lines. A short first line without a full stop,
     * followed by more text, is shown as a sub-heading.
     */
    private void renderArticle(String body) {
        articleBody.getChildren().clear();
        if (Ui.isBlank(body)) {
            return;
        }
        for (String block : body.trim().split("\\R\\s*\\R")) {
            String text = block.trim();
            int lineEnd = text.indexOf('\n');
            if (lineEnd > 0) {
                String first = text.substring(0, lineEnd).trim();
                if (first.length() <= 70 && !first.endsWith(".") && !first.endsWith(",")) {
                    Label heading = Ui.label(first, "section-title");
                    heading.setWrapText(true);
                    articleBody.getChildren().add(heading);
                    text = text.substring(lineEnd + 1).trim();
                }
            }
            Label paragraph = Ui.label(text, "reading-text");
            paragraph.setWrapText(true);
            articleBody.getChildren().add(paragraph);
        }
    }

    @FXML
    private void handleOpen() {
        String url = item.getUrl();
        // Desktop.browse can block for a moment; keep it off the UI thread.
        Thread opener = new Thread(() -> {
            try {
                Desktop.getDesktop().browse(URI.create(url));
            } catch (Exception e) {
                Alerts.error("Couldn't open the browser", "Copy the link and open it manually:\n" + url);
            }
        }, "open-link");
        opener.setDaemon(true);
        opener.start();
    }

    @FXML
    private void handleCopy() {
        ClipboardContent content = new ClipboardContent();
        content.putString(item.getUrl());
        Clipboard.getSystemClipboard().setContent(content);
        copyButton.setText("Link copied");
    }

    @FXML
    private void handleDelete() {
        if (!Alerts.confirm("Delete content", "Delete \"" + item.getTitle() + "\" from the library? This can't be undone.")) {
            return;
        }
        try {
            learningDao.delete(item.getId(), SessionManager.getInstance().getCurrentUserId());
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        Navigator.show(View.CONTENT);
    }

    @FXML
    private void handleBack() {
        Navigator.show(View.CONTENT);
    }
}
