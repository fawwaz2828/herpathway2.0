package com.herpathway.controller;

import com.herpathway.dao.LearningDao;
import com.herpathway.dao.UserDao;
import com.herpathway.model.LearningItem;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

/** Learning library: browse by type, search, and (for mentors and organizations) publish. */
public class ContentController {

    private static final int PAGE_SIZE = 60;
    private static final double CARD_WIDTH = 264;

    @FXML
    private Button publishButton;
    @FXML
    private FlowPane typeChips;
    @FXML
    private TextField searchField;
    @FXML
    private FlowPane itemGrid;

    private final LearningDao learningDao = new LearningDao();
    private final ToggleGroup types = new ToggleGroup();
    private String selectedType;

    @FXML
    public void initialize() {
        // Mentees read; mentors and organizations also publish.
        Ui.setShown(publishButton, canPublish());

        addChip("All", null);
        addChip("Articles", LearningItem.ARTICLE);
        addChip("Videos", LearningItem.VIDEO);
        addChip("E-books", LearningItem.BOOK);
        types.selectedToggleProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                old.setSelected(true);
                return;
            }
            selectedType = (String) selected.getUserData();
            reload();
        });
        searchField.textProperty().addListener((obs, old, query) -> reload());
        reload();
    }

    static boolean canPublish() {
        return !UserDao.MENTEE.equals(SessionManager.getInstance().getCurrentUserType());
    }

    private void addChip(String text, String type) {
        ToggleButton chip = new ToggleButton(text);
        chip.getStyleClass().add("chip");
        chip.setUserData(type);
        chip.setToggleGroup(types);
        chip.setSelected(type == null);
        typeChips.getChildren().add(chip);
    }

    private void reload() {
        itemGrid.getChildren().clear();
        List<LearningItem> items;
        try {
            items = learningDao.find(selectedType, searchField.getText(), PAGE_SIZE);
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        if (items.isEmpty()) {
            itemGrid.getChildren().add(Ui.emptyState("Nothing here yet",
                    Ui.isBlank(searchField.getText()) ? "Content of this type hasn't been published yet."
                            : "No content matches your search."));
            return;
        }
        for (LearningItem item : items) {
            itemGrid.getChildren().add(card(item, CARD_WIDTH));
        }
    }

    /** Also used by the dashboard for its "new in Learning" list. */
    static Node card(LearningItem item, double width) {
        Label type = Ui.badge(item.typeLabel(), badgeVariant(item.getType()));
        Label title = Ui.label(item.getTitle(), "card-title");
        title.setWrapText(true);
        Label summary = Ui.label(Ui.isBlank(item.getSummary()) ? "" : item.getSummary(), "body-text");
        summary.setWrapText(true);
        summary.setMaxHeight(76);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label meta = Ui.label(item.getAuthorName() + "  ·  "
                + (item.isArticle() ? item.readingMinutes() + " min read" : "External link"), "muted", "small");

        VBox card = new VBox(10, new HBox(type), title, summary, spacer, meta);
        card.getStyleClass().addAll("card", "card-hover", "clickable");
        card.setPrefWidth(width);
        card.setMinHeight(190);
        card.setOnMouseClicked(e -> open(item.getId()));
        return card;
    }

    static String badgeVariant(String type) {
        if (LearningItem.VIDEO.equals(type)) {
            return "badge-warning";
        }
        return LearningItem.BOOK.equals(type) ? "badge-info" : null;
    }

    static void open(long itemId) {
        Navigator.<ContentDetailController>show(View.CONTENT_DETAIL, c -> c.setItem(itemId));
    }

    @FXML
    private void handlePublish() {
        Navigator.show(View.CONTENT_EDITOR);
    }
}
