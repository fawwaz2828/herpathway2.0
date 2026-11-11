package com.herpathway.controller;

import com.herpathway.dao.MentorDao;
import com.herpathway.model.MentorData;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Mentee's "find a mentor" grid with search. */
public class MentorMenuController {

    private static final double CARD_WIDTH = 250;

    @FXML
    private FlowPane mentorGrid;
    @FXML
    private TextField searchField;
    @FXML
    private Label resultCountLabel;

    private final MentorDao mentorDao = new MentorDao();
    private List<MentorData> mentors = new ArrayList<>();

    @FXML
    public void initialize() {
        try {
            mentors = mentorDao.findAllCards();
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
        }
        display(mentors);
        searchField.textProperty().addListener((obs, oldValue, query) -> display(filter(query)));
    }

    private List<MentorData> filter(String query) {
        if (query == null || query.trim().isEmpty()) {
            return mentors;
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        return mentors.stream()
                .filter(m -> contains(m.getDisplayName(), q)
                        || contains(m.getFieldOfStudy(), q)
                        || contains(m.getLastEducationLevel(), q)
                        || contains(m.getCompanyName(), q)
                        || contains(m.getCurrentJob(), q))
                .collect(Collectors.toList());
    }

    private static boolean contains(String value, String lowerQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(lowerQuery);
    }

    private void display(List<MentorData> list) {
        mentorGrid.getChildren().clear();
        if (mentors.isEmpty()) {
            resultCountLabel.setText("");
            mentorGrid.getChildren().add(Ui.emptyState("No mentors yet",
                    "Mentors will show up here as soon as they finish signing up."));
            return;
        }
        resultCountLabel.setText(list.size() == 1 ? "1 mentor" : list.size() + " mentors");
        if (list.isEmpty()) {
            mentorGrid.getChildren().add(Ui.emptyState("No matches", "Try a different name, field or company."));
            return;
        }
        for (MentorData mentor : list) {
            mentorGrid.getChildren().add(card(mentor));
        }
    }

    private Node card(MentorData mentor) {
        Label name = new Label(mentor.getDisplayName());
        name.getStyleClass().add("card-title");
        name.setWrapText(true);

        Label job = new Label(joinNonEmpty(mentor.getCurrentJob(), mentor.getCompanyName(), " at "));
        job.getStyleClass().add("muted");
        job.setWrapText(true);

        Label field = Ui.badge(orDash(mentor.getFieldOfStudy()), null);
        Label education = Ui.badge(orDash(mentor.getLastEducationLevel()), "badge-muted");
        FlowPane tags = new FlowPane(6, 6, field, education);
        tags.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button view = new Button("View profile");
        view.getStyleClass().add("btn-secondary");
        view.setMaxWidth(Double.MAX_VALUE);
        view.setOnAction(e -> openProfile(mentor));

        VBox card = new VBox(10, Ui.avatar(mentor.getProfilePicture(), mentor.getDisplayName(), 72), name, job, tags, spacer, view);
        card.getStyleClass().addAll("card", "card-hover", "clickable");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(CARD_WIDTH);
        card.setMinHeight(290);
        name.setAlignment(Pos.CENTER);
        job.setAlignment(Pos.CENTER);
        card.setOnMouseClicked(e -> openProfile(mentor));
        return card;
    }

    private static String joinNonEmpty(String a, String b, String sep) {
        boolean hasA = a != null && !a.isEmpty();
        boolean hasB = b != null && !b.isEmpty();
        if (hasA && hasB) {
            return a + sep + b;
        }
        return hasA ? a : hasB ? b : "";
    }

    private static String orDash(String s) {
        return s == null || s.isEmpty() ? "-" : s;
    }

    private void openProfile(MentorData mentor) {
        Navigator.<ProfilMentorController>show(View.MENTOR_PUBLIC_PROFILE, c -> c.setMentorData(mentor));
    }
}
