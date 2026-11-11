package com.herpathway.controller;

import com.herpathway.dao.MenteeDao;
import com.herpathway.dao.MentorshipDao;
import com.herpathway.dao.QuestionnaireDao;
import com.herpathway.model.MenteeProfile;
import com.herpathway.model.Questionnaire;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Images;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.SQLException;

/** Popup where a mentor reviews and accepts a mentee's application. */
public class MenteeApplicationController {

    @FXML private ImageView profileImage;
    @FXML private Label displayNameLabel;
    @FXML private Label realNameLabel;
    @FXML private Label experienceBadge;
    @FXML private Label statusBadge;
    @FXML private Label aspectsLabel;
    @FXML private Label backgroundLabel;
    @FXML private Label phoneNumberLabel;
    @FXML private Label emailLabel;
    @FXML private Label addressLabel;
    @FXML private Label instagramLabel;
    @FXML private Label linkedInLabel;
    @FXML private Label lastEducationLevelLabel;
    @FXML private Label educationalInstitutionLabel;
    @FXML private Label fieldOfStudyLabel;
    @FXML private Label graduationYearLabel;
    @FXML private Label currentJobLabel;
    @FXML private Label companyNameLabel;
    @FXML private Label positionLabel;
    @FXML private Label workExperienceLabel;
    @FXML private Label mainGoalLabel;
    @FXML private Label fieldOfStudyGoalLabel;
    @FXML private Label expectationsLabel;
    @FXML private Button acceptButton;
    @FXML private Button declineButton;

    private final MenteeDao menteeDao = new MenteeDao();
    private final MentorshipDao mentorshipDao = new MentorshipDao();
    private final QuestionnaireDao questionnaireDao = new QuestionnaireDao();

    private Questionnaire application;
    private Runnable onAccepted;

    /**
     * @param onAccepted run after the mentee is accepted or declined (the request list uses it to refresh)
     */
    public void setApplication(Questionnaire q, Runnable onAccepted) {
        this.application = q;
        this.onAccepted = onAccepted;

        experienceBadge.setText(q.getExperienceLevel());
        aspectsLabel.setText(orDash(q.getBeneficialAspects()));
        backgroundLabel.setText(orDash(q.getSuggestions()));
        if (q.isAcceptedByThisMentor()) {
            setStatus("Accepted", "badge-success");
            acceptButton.setDisable(true);
            acceptButton.setText("Already accepted");
            Ui.setShown(declineButton, false);
        } else if (q.isDeclined()) {
            setStatus("Declined", "badge-danger");
            acceptButton.setText("Accept after all");
            Ui.setShown(declineButton, false);
        } else if (q.isTakenByAnotherMentor()) {
            setStatus("Has another mentor", "badge-muted");
            acceptButton.setText("Accept anyway");
        } else {
            setStatus("New request", "badge-warning");
        }

        try {
            MenteeProfile p = menteeDao.findByUserId(q.getMenteeId());
            if (p != null) {
                show(p);
            }
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
        }
    }

    private void show(MenteeProfile p) {
        Ui.avatar(profileImage, Images.fromPath(p.getProfilePicturePath()));
        displayNameLabel.setText(p.getDisplayName());
        realNameLabel.setText(p.getRealName());
        phoneNumberLabel.setText(orDash(p.getPhoneNumber()));
        emailLabel.setText(orDash(p.getEmail()));
        addressLabel.setText(orDash(p.getAddress()));
        instagramLabel.setText(orDash(p.getInstagram()));
        linkedInLabel.setText(orDash(p.getLinkedIn()));
        lastEducationLevelLabel.setText(orDash(p.getLastEducationLevel()));
        educationalInstitutionLabel.setText(orDash(p.getEducationalInstitution()));
        fieldOfStudyLabel.setText(orDash(p.getFieldOfStudy()));
        graduationYearLabel.setText(p.getGraduationYear() == 0 ? "-" : String.valueOf(p.getGraduationYear()));
        currentJobLabel.setText(orDash(p.getCurrentJob()));
        companyNameLabel.setText(orDash(p.getCompanyName()));
        positionLabel.setText(orDash(p.getPosition()));
        workExperienceLabel.setText(orDash(p.getWorkExperience()));
        mainGoalLabel.setText(orDash(p.getMainGoal()));
        fieldOfStudyGoalLabel.setText(orDash(p.getFieldOfStudyGoal()));
        expectationsLabel.setText(orDash(p.getExpectations()));
    }

    @FXML
    private void handleAcceptButtonAction() {
        long menteeId = application.getMenteeId();
        try {
            mentorshipDao.accept(menteeId, SessionManager.getInstance().getCurrentUserId());
        } catch (SQLException e) {
            Alerts.error("Couldn't accept mentee", e);
            return;
        }
        close();
        if (onAccepted != null) {
            onAccepted.run();
        }
        // Take the mentor straight to the conversation with the new mentee.
        Navigator.<MentorChatSessionController>show(View.MENTOR_CHAT, c -> c.setMenteeId(menteeId));
    }

    @FXML
    private void handleDeclineButtonAction() {
        if (!Alerts.confirm("Decline application", "Decline this application? The mentee will see it as declined.")) {
            return;
        }
        try {
            questionnaireDao.decline(application.getMenteeId(), SessionManager.getInstance().getCurrentUserId());
        } catch (SQLException e) {
            Alerts.error("Couldn't decline application", e);
            return;
        }
        close();
        if (onAccepted != null) {
            onAccepted.run();
        }
    }

    @FXML
    private void handleCloseButtonAction() {
        close();
    }

    private void close() {
        ((Stage) acceptButton.getScene().getWindow()).close();
    }

    private void setStatus(String text, String variant) {
        statusBadge.setText(text);
        statusBadge.getStyleClass().add(variant);
    }

    private static String orDash(String s) {
        return s == null || s.trim().isEmpty() ? "-" : s;
    }
}
