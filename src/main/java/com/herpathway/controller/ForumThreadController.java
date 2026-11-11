package com.herpathway.controller;

import com.herpathway.dao.ForumDao;
import com.herpathway.model.ForumReply;
import com.herpathway.model.ForumThread;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

/** One forum discussion: read, like, reply, and delete your own posts. */
public class ForumThreadController {

    private static final int MAX_REPLY_LENGTH = 5000;

    @FXML
    private StackPane authorAvatar;
    @FXML
    private Label authorLabel;
    @FXML
    private Label metaLabel;
    @FXML
    private Label categoryBadge;
    @FXML
    private Label titleLabel;
    @FXML
    private Label bodyLabel;
    @FXML
    private Button likeButton;
    @FXML
    private Label replyCountLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private VBox replyList;
    @FXML
    private TextArea replyArea;
    @FXML
    private Label errorLabel;

    private final ForumDao forumDao = new ForumDao();
    private long threadId;

    public void setThread(long threadId) {
        this.threadId = threadId;
        reload();
    }

    private long me() {
        return SessionManager.getInstance().getCurrentUserId();
    }

    private void reload() {
        ForumThread thread;
        List<ForumReply> replies;
        try {
            thread = forumDao.findThread(threadId, me());
            if (thread == null) {
                Alerts.info("Discussion removed", "This discussion no longer exists.");
                Navigator.show(View.FORUM);
                return;
            }
            replies = forumDao.findReplies(threadId);
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }

        authorAvatar.getChildren().setAll(Ui.avatar(null, thread.getAuthorName(), 44));
        authorLabel.setText(thread.getAuthorName());
        metaLabel.setText(Ui.roleLabel(thread.getAuthorRole()) + "  ·  " + Ui.timeAgo(thread.getCreatedAt()));
        categoryBadge.setText(thread.getCategory());
        titleLabel.setText(thread.getTitle());
        bodyLabel.setText(thread.getBody());

        likeButton.setText((thread.isLikedByViewer() ? "♥  Liked" : "♡  Like") + "  ·  " + thread.getLikeCount());
        likeButton.getStyleClass().setAll("button", thread.isLikedByViewer() ? "btn-secondary" : "btn-outline");
        replyCountLabel.setText(ForumController.count(replies.size(), "reply", "replies"));
        Ui.setShown(deleteButton, thread.getAuthorId() == me());

        replyList.getChildren().clear();
        for (ForumReply reply : replies) {
            replyList.getChildren().add(row(reply, thread.getAuthorId()));
        }
    }

    private Node row(ForumReply r, long threadAuthorId) {
        Label name = Ui.label(r.getAuthorName(), "strong");
        Label meta = Ui.label(Ui.roleLabel(r.getAuthorRole()) + "  ·  " + Ui.timeAgo(r.getCreatedAt()), "muted", "small");
        HBox header = new HBox(8, name, meta);
        header.setAlignment(Pos.CENTER_LEFT);
        if (r.getAuthorId() == threadAuthorId) {
            header.getChildren().add(Ui.badge("Author", null));
        }
        if (r.getAuthorId() == me()) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button delete = new Button("Delete");
            delete.getStyleClass().addAll("btn-danger", "btn-small");
            delete.setOnAction(e -> deleteReply(r));
            header.getChildren().addAll(spacer, delete);
        }
        Label body = Ui.label(r.getBody(), "reading-text");
        body.setWrapText(true);

        VBox text = new VBox(6, header, body);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox row = new HBox(12, Ui.avatar(null, r.getAuthorName(), 34), text);
        row.getStyleClass().add("card");
        return row;
    }

    @FXML
    private void handleLike() {
        try {
            forumDao.toggleLike(threadId, me());
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        reload();
    }

    @FXML
    private void handleReply() {
        String text = replyArea.getText() == null ? "" : replyArea.getText().trim();
        if (text.isEmpty()) {
            showError("Write something before posting.");
            return;
        }
        if (text.length() > MAX_REPLY_LENGTH) {
            showError("Replies can be at most " + MAX_REPLY_LENGTH + " characters.");
            return;
        }
        try {
            forumDao.addReply(threadId, me(), text);
        } catch (SQLException e) {
            Alerts.error("Couldn't post reply", e);
            return;
        }
        replyArea.clear();
        Ui.setShown(errorLabel, false);
        reload();
    }

    private void deleteReply(ForumReply reply) {
        if (!Alerts.confirm("Delete reply", "Delete your reply? This can't be undone.")) {
            return;
        }
        try {
            forumDao.deleteReply(reply.getId(), me());
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        reload();
    }

    @FXML
    private void handleDelete() {
        if (!Alerts.confirm("Delete discussion", "Delete this discussion and all its replies? This can't be undone.")) {
            return;
        }
        try {
            forumDao.deleteThread(threadId, me());
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        Navigator.show(View.FORUM);
    }

    @FXML
    private void handleBack() {
        Navigator.show(View.FORUM);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        Ui.setShown(errorLabel, true);
    }
}
