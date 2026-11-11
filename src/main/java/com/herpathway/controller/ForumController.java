package com.herpathway.controller;

import com.herpathway.dao.ForumDao;
import com.herpathway.model.ForumThread;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

/** Forum landing page: discussions, newest first, filtered by category and search text. */
public class ForumController {

    private static final int PAGE_SIZE = 50;

    /** Remembered while the app runs, so "back" from a thread returns to the same filter. */
    private static String lastCategory;

    @FXML
    private FlowPane categoryChips;
    @FXML
    private TextField searchField;
    @FXML
    private VBox threadList;

    private final ForumDao forumDao = new ForumDao();
    private final ToggleGroup categories = new ToggleGroup();

    @FXML
    public void initialize() {
        addChip("All", null);
        for (String category : ForumThread.CATEGORIES) {
            addChip(category, category);
        }
        categories.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                old.setSelected(true); // a filter is always selected
                return;
            }
            lastCategory = (String) selected.getUserData();
            reload();
        });
        searchField.textProperty().addListener((obs, old, query) -> reload());
        reload();
    }

    private void addChip(String text, String category) {
        ToggleButton chip = new ToggleButton(text);
        chip.getStyleClass().add("chip");
        chip.setUserData(category);
        chip.setToggleGroup(categories);
        chip.setSelected(category == null ? lastCategory == null : category.equals(lastCategory));
        categoryChips.getChildren().add(chip);
    }

    private void reload() {
        threadList.getChildren().clear();
        List<ForumThread> threads;
        try {
            threads = forumDao.findThreads(SessionManager.getInstance().getCurrentUserId(), lastCategory,
                    searchField.getText(), PAGE_SIZE);
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        if (threads.isEmpty()) {
            boolean filtered = lastCategory != null || !Ui.isBlank(searchField.getText());
            VBox empty = filtered
                    ? Ui.emptyState("No discussions found", "Try another category or a different search.")
                    : Ui.emptyState("No discussions yet", "Be the first to start one. Ask a question or share something you learned.");
            empty.getStyleClass().add("card");
            threadList.getChildren().add(empty);
            return;
        }
        for (ForumThread thread : threads) {
            threadList.getChildren().add(row(thread));
        }
    }

    /** Also used by the dashboard for its "latest discussions" list. */
    static Node row(ForumThread t) {
        Label title = Ui.label(t.getTitle(), "card-title");
        title.setWrapText(true);
        Label meta = Ui.label(t.getAuthorName() + "  ·  " + Ui.roleLabel(t.getAuthorRole()) + "  ·  "
                + Ui.timeAgo(t.getCreatedAt()), "muted", "small");
        Label snippet = Ui.label(snippet(t.getBody()), "body-text");
        snippet.setWrapText(true);
        snippet.setMaxHeight(38);

        HBox footer = new HBox(10, Ui.badge(t.getCategory(), "badge-muted"),
                Ui.label(count(t.getReplyCount(), "reply", "replies"), "muted", "small"),
                Ui.label(count(t.getLikeCount(), "like", "likes"), "muted", "small"));
        footer.setAlignment(Pos.CENTER_LEFT);

        VBox text = new VBox(6, title, meta, snippet, footer);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox row = new HBox(14, Ui.avatar(null, t.getAuthorName(), 40), text);
        row.getStyleClass().addAll("card", "card-hover", "clickable");
        row.setOnMouseClicked(e -> open(t.getId()));
        return row;
    }

    static void open(long threadId) {
        Navigator.<ForumThreadController>show(View.FORUM_THREAD, c -> c.setThread(threadId));
    }

    private static String snippet(String body) {
        String flat = body == null ? "" : body.replaceAll("\\s+", " ").trim();
        return flat.length() <= 220 ? flat : flat.substring(0, 220).trim() + "...";
    }

    static String count(int n, String singular, String plural) {
        return n + " " + (n == 1 ? singular : plural);
    }

    @FXML
    private void handleNewThread() {
        Navigator.show(View.FORUM_COMPOSE);
    }
}
