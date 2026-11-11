package com.herpathway.controller;

import com.herpathway.dao.MentorDao;
import com.herpathway.dao.MentorshipDao;
import com.herpathway.dao.QuestionnaireDao;
import com.herpathway.model.MentorData;
import com.herpathway.model.MentorProfile;
import com.herpathway.model.Questionnaire;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.sql.SQLException;

/** A mentor's public profile, as seen by a mentee. */
public class ProfilMentorController {

    @FXML
    private Label displayNameLabel;
    @FXML
    private Label fieldOfStudyLabel;
    @FXML
    private Label lastEducationLevelLabel;
    @FXML
    private Label companyNameLabel;
    @FXML
    private Label currentJobLabel;
    @FXML
    private Button mentorshipQuestionButton;
    @FXML
    private Label applyHintLabel;
    @FXML
    private ImageView profilePictureView;
    @FXML
    private Label institutionLabel;
    @FXML
    private Label fieldDetailLabel;
    @FXML
    private Label educationDetailLabel;
    @FXML
    private Label graduationLabel;
    @FXML
    private Label positionLabel;
    @FXML
    private Label companyDetailLabel;
    @FXML
    private Label experienceLabel;
    @FXML
    private Label linkedInLabel;

    private final MentorDao mentorDao = new MentorDao();
    private final MentorshipDao mentorshipDao = new MentorshipDao();
    private final QuestionnaireDao questionnaireDao = new QuestionnaireDao();

    private MentorData mentorData;
    private boolean isMyMentor;

    public void setMentorData(MentorData mentor) {
        if (mentor == null) {
            return;
        }
        this.mentorData = mentor;
        displayNameLabel.setText(mentor.getDisplayName());
        currentJobLabel.setText(orDash(mentor.getCurrentJob()));
        companyNameLabel.setText(mentor.getCompanyName() == null ? "" : "at " + mentor.getCompanyName());
        fieldOfStudyLabel.setText(orDash(mentor.getFieldOfStudy()));
        lastEducationLevelLabel.setText(orDash(mentor.getLastEducationLevel()));
        Ui.avatar(profilePictureView, mentor.getProfilePicture());

        try {
            MentorProfile full = mentorDao.findByUserId(mentor.getUserId());
            if (full != null) {
                institutionLabel.setText(orDash(full.getEducationalInstitution()));
                fieldDetailLabel.setText(orDash(full.getFieldOfStudy()));
                educationDetailLabel.setText(orDash(full.getLastEducationLevel()));
                graduationLabel.setText(full.getGraduationYear() == 0 ? "-" : String.valueOf(full.getGraduationYear()));
                positionLabel.setText(orDash(full.getPosition()));
                companyDetailLabel.setText(orDash(full.getCompanyName()));
                experienceLabel.setText(orDash(full.getWorkExperience()));
                linkedInLabel.setText(orDash(full.getLinkedIn()));
            }
            updateApplyButton();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Apply / already applied / already your mentor. */
    private void updateApplyButton() throws SQLException {
        long me = SessionManager.getInstance().getCurrentUserId();
        isMyMentor = mentorshipDao.findMentorOf(me) == mentorData.getUserId();
        if (isMyMentor) {
            mentorshipQuestionButton.setText("Open chat");
            applyHintLabel.setText("This is your mentor.");
            return;
        }
        Questionnaire application = questionnaireDao.find(me, mentorData.getUserId());
        if (application == null) {
            applyHintLabel.setText("Takes about 2 minutes.");
        } else if (application.isDeclined()) {
            mentorshipQuestionButton.setText("Application declined");
            mentorshipQuestionButton.setDisable(true);
            applyHintLabel.setText("This mentor can't take you on right now. Try another mentor.");
        } else {
            mentorshipQuestionButton.setText("Application sent");
            mentorshipQuestionButton.setDisable(true);
            applyHintLabel.setText("Waiting for the mentor to review your application.");
        }
    }

    @FXML
    private void handleBackButtonAction() {
        Navigator.show(View.MENTOR_MENU);
    }

    @FXML
    private void handleMentorshipQuestionButtonAction() {
        if (isMyMentor) {
            Navigator.show(View.MENTEE_CHAT);
            return;
        }
        // The questionnaire reads the chosen mentor from the session.
        SessionManager.getInstance().setCurrentMentorData(mentorData);
        Navigator.show(View.MENTORSHIP_QUESTION);
    }

    private static String orDash(String s) {
        return s == null || s.trim().isEmpty() ? "-" : s;
    }
}
