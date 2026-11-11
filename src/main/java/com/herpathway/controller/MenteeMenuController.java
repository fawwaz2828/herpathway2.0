package com.herpathway.controller;

import com.herpathway.dao.QuestionnaireDao;
import com.herpathway.model.Questionnaire;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Images;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

/** Mentor's list of incoming mentee applications. */
public class MenteeMenuController {

    @FXML
    private VBox requestList;
    @FXML
    private Label pendingCountLabel;

    private final QuestionnaireDao questionnaireDao = new QuestionnaireDao();

    @FXML
    public void initialize() {
        reload();
    }

    /** Reloads the list (also called after accepting a mentee in the popup). */
    public void reload() {
        requestList.getChildren().clear();
        List<Questionnaire> requests;
        try {
            requests = questionnaireDao.findByMentor(SessionManager.getInstance().getCurrentUserId());
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }

        long pending = requests.stream().filter(Questionnaire::isPending).count();
        pendingCountLabel.setText(pending + " new");
        pendingCountLabel.setVisible(pending > 0);

        if (requests.isEmpty()) {
            VBox empty = Ui.emptyState("No requests yet",
                    "When a mentee applies to you, their application will show up here.");
            empty.getStyleClass().add("card");
            requestList.getChildren().add(empty);
            return;
        }
        for (Questionnaire q : requests) {
            requestList.getChildren().add(row(q));
        }
    }

    private Node row(Questionnaire q) {
        String name = q.getMenteeName() != null ? q.getMenteeName() : "Mentee #" + q.getMenteeId();
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("card-title");

        Label status;
        if (q.isAcceptedByThisMentor()) {
            status = Ui.badge("Accepted", "badge-success");
        } else if (q.isDeclined()) {
            status = Ui.badge("Declined", "badge-danger");
        } else if (q.isTakenByAnotherMentor()) {
            status = Ui.badge("Has another mentor", "badge-muted");
        } else {
            status = Ui.badge("New", "badge-warning");
        }
        Label when = Ui.label(Ui.timeAgo(q.getCreatedAt()), "muted", "small");
        HBox titleRow = new HBox(10, nameLabel, status, when);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        FlowPane tags = new FlowPane(6, 6);
        tags.getChildren().add(Ui.badge(q.getExperienceLevel(), "badge-muted"));
        if (q.getBeneficialAspects() != null) {
            for (String aspect : q.getBeneficialAspects().split("\\s*,\\s*")) {
                String trimmed = aspect.replace("\n", "").trim();
                if (!trimmed.isEmpty()) {
                    tags.getChildren().add(Ui.badge(trimmed, null));
                }
            }
        }

        Label note = new Label(q.getSuggestions() == null || q.getSuggestions().trim().isEmpty()
                ? "No background note." : q.getSuggestions().trim());
        note.getStyleClass().add("muted");
        note.setWrapText(true);
        note.setMaxHeight(40);

        VBox text = new VBox(8, titleRow, tags, note);
        HBox.setHgrow(text, Priority.ALWAYS);

        Button view = new Button(q.isAcceptedByThisMentor() ? "Open chat" : "Review");
        view.getStyleClass().add(q.isPending() ? "btn-primary" : "btn-secondary");
        view.setMinWidth(Button.USE_PREF_SIZE);
        view.setOnAction(e -> {
            if (q.isAcceptedByThisMentor()) {
                Navigator.<MentorChatSessionController>show(View.MENTOR_CHAT, c -> c.setMenteeId(q.getMenteeId()));
            } else {
                openApplication(q);
            }
        });

        HBox row = new HBox(16, Ui.avatar(Images.fromPathOrNull(q.getMenteePicturePath()), name, 48), text, view);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("card");
        return row;
    }

    private void openApplication(Questionnaire q) {
        Navigator.<MenteeApplicationController>openWindow(View.MENTEE_APPLICATION, "Mentee application",
                c -> c.setApplication(q, this::reload));
    }
}
