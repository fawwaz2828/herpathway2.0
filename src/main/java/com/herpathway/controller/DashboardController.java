package com.herpathway.controller;

import com.herpathway.dao.DashboardDao;
import com.herpathway.dao.ForumDao;
import com.herpathway.dao.LearningDao;
import com.herpathway.dao.MentorshipDao;
import com.herpathway.dao.QuestionnaireDao;
import com.herpathway.dao.UserDao;
import com.herpathway.dao.UserDirectoryDao;
import com.herpathway.model.ContactSummary;
import com.herpathway.model.ForumThread;
import com.herpathway.model.LearningItem;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

/** Home dashboard: what needs the user's attention, plus recent forum and learning activity. */
public class DashboardController {

    @FXML
    private Label greetingLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private HBox statRow;
    @FXML
    private VBox sections;
    @FXML
    private VBox discussionList;
    @FXML
    private VBox learningList;

    private final DashboardDao stats = new DashboardDao();
    private final QuestionnaireDao questionnaireDao = new QuestionnaireDao();
    private final MentorshipDao mentorshipDao = new MentorshipDao();
    private final SessionManager session = SessionManager.getInstance();

    @FXML
    public void initialize() {
        long me = session.getCurrentUserId();
        String role = session.getCurrentUserType();
        try {
            ContactSummary self = new UserDirectoryDao().find(me);
            String name = self != null ? self.getDisplayName() : session.getCurrentUserUsername();
            greetingLabel.setText(greeting() + ", " + name);

            if (UserDao.MENTOR.equals(role)) {
                buildMentor(me);
            } else if (UserDao.ORGANIZATION.equals(role)) {
                buildOrganization(me);
            } else {
                buildMentee(me);
            }
            fillDiscussions(me);
            fillLearning();
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
        }
    }

    private static String greeting() {
        int hour = LocalTime.now().getHour();
        return hour < 12 ? "Good morning" : hour < 18 ? "Good afternoon" : "Good evening";
    }

    // ---- per role ------------------------------------------------------------------------

    private void buildMentee(long me) throws SQLException {
        subtitleLabel.setText("Here is where your mentorship stands today.");
        List<ContactSummary> mentor = mentorshipDao.findMentorContactOf(me);
        List<Questionnaire> applications = questionnaireDao.findByMentee(me);
        long pending = applications.stream().filter(Questionnaire::isPending).count();

        statRow.getChildren().setAll(
                stat("Your mentor", mentor.isEmpty() ? "Not yet" : mentor.get(0).getDisplayName(),
                        mentor.isEmpty() ? "Apply to a mentor to get started" : "Mentorship active"),
                stat("Applications", String.valueOf(applications.size()), pending + " waiting for a reply"),
                stat("Mentors available", String.valueOf(stats.countMentors()), "Women working in STEM"),
                stat("Learning library", String.valueOf(stats.countLearningItems()), "Articles, videos and books"));

        if (!mentor.isEmpty()) {
            ContactSummary m = mentor.get(0);
            sections.getChildren().add(actionCard(Ui.avatar(m.getPicture(), m.getDisplayName(), 48),
                    m.getDisplayName(), "Your mentor. Keep the conversation going.",
                    "Open chat", () -> Navigator.show(View.MENTEE_CHAT)));
        } else if (applications.isEmpty()) {
            sections.getChildren().add(actionCard(null, "Find your mentor",
                    "Browse mentor profiles and send an application. It takes about two minutes.",
                    "Browse mentors", () -> Navigator.show(View.MENTOR_MENU)));
        }

        if (!applications.isEmpty()) {
            VBox list = new VBox();
            for (Questionnaire q : applications) {
                Label status;
                if (q.isAcceptedByThisMentor()) {
                    status = Ui.badge("Accepted", "badge-success");
                } else if (q.isDeclined()) {
                    status = Ui.badge("Declined", "badge-danger");
                } else {
                    status = Ui.badge("Waiting for reply", "badge-warning");
                }
                String name = Ui.isBlank(q.getMentorName()) ? "Mentor" : q.getMentorName();
                String meta = Ui.isBlank(q.getMentorHeadline()) ? "Applied " + Ui.timeAgo(q.getCreatedAt())
                        : q.getMentorHeadline() + "  ·  applied " + Ui.timeAgo(q.getCreatedAt());
                list.getChildren().add(listRow(Ui.avatar(null, name, 36), name, meta, status, null));
            }
            sections.getChildren().add(section("Your applications", list));
        }
    }

    private void buildMentor(long me) throws SQLException {
        subtitleLabel.setText("Here is what needs your attention.");
        int pending = stats.countPendingRequests(me);
        statRow.getChildren().setAll(
                stat("Pending requests", String.valueOf(pending), pending == 0 ? "You're all caught up" : "Waiting for your decision"),
                stat("Active mentees", String.valueOf(stats.countMenteesOf(me)), "Mentees you accepted"),
                stat("Your discussions", String.valueOf(stats.countThreadsBy(me)), "Started in the forum"),
                stat("Published content", String.valueOf(stats.countLearningItemsBy(me)), "In the learning library"));

        VBox list = new VBox();
        for (Questionnaire q : questionnaireDao.findByMentor(me)) {
            if (!q.isPending()) {
                continue;
            }
            String name = Ui.isBlank(q.getMenteeName()) ? "Mentee #" + q.getMenteeId() : q.getMenteeName();
            Button review = new Button("Review");
            review.getStyleClass().addAll("btn-secondary", "btn-small");
            review.setOnAction(e -> Navigator.show(View.MENTEE_MENU));
            list.getChildren().add(listRow(
                    Ui.avatar(Images.fromPathOrNull(q.getMenteePicturePath()), name, 36), name,
                    q.getExperienceLevel() + "  ·  applied " + Ui.timeAgo(q.getCreatedAt()), review, null));
        }
        if (list.getChildren().isEmpty()) {
            list.getChildren().add(Ui.label("No requests are waiting. New applications will appear here.", "muted", "list-empty"));
        }
        sections.getChildren().add(section("Requests waiting for you", list));
    }

    private void buildOrganization(long me) throws SQLException {
        subtitleLabel.setText("Support the community by sharing resources and joining discussions.");
        statRow.getChildren().setAll(
                stat("Published content", String.valueOf(stats.countLearningItemsBy(me)), "By your organization"),
                stat("Your discussions", String.valueOf(stats.countThreadsBy(me)), "Started in the forum"),
                stat("Mentors", String.valueOf(stats.countMentors()), "On the platform"),
                stat("Mentees", String.valueOf(stats.countMentees()), "On the platform"));
        sections.getChildren().add(actionCard(null, "Share a resource",
                "Publish an article, or recommend a video or book to mentees and mentors.",
                "Publish content", () -> Navigator.show(View.CONTENT_EDITOR)));
    }

    // ---- shared lists ---------------------------------------------------------------------

    private void fillDiscussions(long me) throws SQLException {
        List<ForumThread> threads = new ForumDao().findThreads(me, null, null, 4);
        if (threads.isEmpty()) {
            discussionList.getChildren().add(Ui.label("No discussions yet. Start the first one in the forum.", "muted", "list-empty"));
        }
        for (ForumThread t : threads) {
            discussionList.getChildren().add(listRow(null, t.getTitle(),
                    t.getAuthorName() + "  ·  " + ForumController.count(t.getReplyCount(), "reply", "replies")
                            + "  ·  " + Ui.timeAgo(t.getCreatedAt()),
                    Ui.badge(t.getCategory(), "badge-muted"), () -> ForumController.open(t.getId())));
        }
    }

    private void fillLearning() throws SQLException {
        List<LearningItem> items = new LearningDao().find(null, null, 4);
        if (items.isEmpty()) {
            learningList.getChildren().add(Ui.label("The library is empty.", "muted", "list-empty"));
        }
        for (LearningItem item : items) {
            learningList.getChildren().add(listRow(null, item.getTitle(), "By " + item.getAuthorName(),
                    Ui.badge(item.typeLabel(), ContentController.badgeVariant(item.getType())),
                    () -> ContentController.open(item.getId())));
        }
    }

    // ---- building blocks ------------------------------------------------------------------

    private static Node stat(String label, String value, String hint) {
        // Numbers are shown large; a name (the mentee's mentor) needs a smaller size to fit.
        Label valueLabel = Ui.label(value, value.length() > 8 ? "stat-value-text" : "stat-value");
        valueLabel.setMaxWidth(Double.MAX_VALUE);
        VBox tile = new VBox(4, Ui.label(label, "stat-label"), valueLabel, Ui.label(hint, "muted", "small"));
        tile.getStyleClass().addAll("card", "stat-tile");
        tile.setPrefWidth(100);
        HBox.setHgrow(tile, Priority.ALWAYS);
        return tile;
    }

    private static Node section(String title, Node content) {
        VBox card = new VBox(6, Ui.label(title, "section-title"), content);
        card.getStyleClass().add("card");
        return card;
    }

    /** A highlighted card with one call to action. */
    private static Node actionCard(Node leading, String title, String message, String buttonText, Runnable action) {
        Label messageLabel = Ui.label(message, "muted");
        messageLabel.setWrapText(true);
        VBox text = new VBox(4, Ui.label(title, "card-title"), messageLabel);
        HBox.setHgrow(text, Priority.ALWAYS);
        Button button = new Button(buttonText);
        button.getStyleClass().add("btn-primary");
        button.setOnAction(e -> action.run());
        HBox card = new HBox(16);
        if (leading != null) {
            card.getChildren().add(leading);
        }
        card.getChildren().addAll(text, button);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().addAll("card", "card-accent");
        return card;
    }

    /** One line in a list card: optional avatar, title + meta, trailing node; clickable when {@code onClick} is given. */
    private static Node listRow(Node leading, String title, String meta, Node trailing, Runnable onClick) {
        Label titleLabel = Ui.label(title, "strong");
        VBox text = new VBox(2, titleLabel, Ui.label(meta, "muted", "small"));
        text.setMinWidth(0);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox row = new HBox(12);
        if (leading != null) {
            row.getChildren().add(leading);
        }
        row.getChildren().add(text);
        if (trailing != null) {
            if (trailing instanceof Region) {
                ((Region) trailing).setMinWidth(Region.USE_PREF_SIZE); // never truncate a badge or button
            }
            row.getChildren().add(trailing);
        }
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("list-row");
        if (onClick != null) {
            row.getStyleClass().add("clickable");
            row.setOnMouseClicked(e -> onClick.run());
        }
        return row;
    }

    @FXML
    private void handleOpenForum() {
        Navigator.show(View.FORUM);
    }

    @FXML
    private void handleOpenLearning() {
        Navigator.show(View.CONTENT);
    }
}
