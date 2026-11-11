package com.herpathway.controller;

import com.herpathway.dao.QuestionnaireDao;
import com.herpathway.model.MentorData;
import com.herpathway.model.Questionnaire;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Mentee's application form for the mentor chosen in {@link ProfilMentorController}. */
public class MentorshipQuestionController {

    @FXML
    private Label titleLabel;
    @FXML
    private RadioButton veryExperiencedRadioButton;
    @FXML
    private RadioButton experiencedRadioButton;
    @FXML
    private RadioButton neutralRadioButton;
    @FXML
    private RadioButton inexperiencedRadioButton;
    @FXML
    private RadioButton veryInexperiencedRadioButton;
    @FXML
    private CheckBox networkingCheckBox;
    @FXML
    private CheckBox learningSkillsCheckBox;
    @FXML
    private CheckBox careerAdviceCheckBox;
    @FXML
    private CheckBox personalDevelopmentCheckBox;
    @FXML
    private TextArea suggestionsTextArea;
    @FXML
    private Label errorLabel;

    private final ToggleGroup experienceLevel = new ToggleGroup();
    private final QuestionnaireDao questionnaireDao = new QuestionnaireDao();

    @FXML
    public void initialize() {
        veryExperiencedRadioButton.setToggleGroup(experienceLevel);
        experiencedRadioButton.setToggleGroup(experienceLevel);
        neutralRadioButton.setToggleGroup(experienceLevel);
        inexperiencedRadioButton.setToggleGroup(experienceLevel);
        veryInexperiencedRadioButton.setToggleGroup(experienceLevel);

        MentorData mentor = SessionManager.getInstance().getCurrentMentorData();
        if (mentor != null) {
            titleLabel.setText("Apply to " + mentor.getDisplayName());
        }
    }

    @FXML
    private void handleSubmitButtonAction() {
        if (experienceLevel.getSelectedToggle() == null) {
            showError("Choose how experienced you are.");
            return;
        }
        if (selectedAspects().isEmpty()) {
            showError("Pick at least one thing you want from mentoring.");
            return;
        }
        SessionManager session = SessionManager.getInstance();
        long menteeId = session.getCurrentUserId();
        long mentorId = session.getCurrentMentorId();
        if (menteeId == 0 || mentorId == 0) {
            showError("No mentor selected. Go back and choose a mentor.");
            return;
        }

        String level = ((RadioButton) experienceLevel.getSelectedToggle()).getText();
        Questionnaire q = new Questionnaire(menteeId, mentorId, level, selectedAspects(), suggestionsTextArea.getText());
        try {
            if (questionnaireDao.exists(menteeId, mentorId)) {
                showError("You have already applied to this mentor.");
                return;
            }
            questionnaireDao.insert(q);
        } catch (SQLException e) {
            Alerts.error("Couldn't send application", e);
            return;
        }

        Alerts.info("Application sent",
                "Your application was sent. You'll be able to chat once the mentor accepts it.");
        Navigator.show(View.MENTOR_MENU);
    }

    @FXML
    private void handleBackButtonAction() {
        MentorData mentor = SessionManager.getInstance().getCurrentMentorData();
        if (mentor == null) {
            Navigator.show(View.MENTOR_MENU);
        } else {
            Navigator.<ProfilMentorController>show(View.MENTOR_PUBLIC_PROFILE, c -> c.setMentorData(mentor));
        }
    }

    @FXML
    private void handleMentorMenuButtonAction() {
        handleBackButtonAction();
    }

    private String selectedAspects() {
        List<String> aspects = new ArrayList<>();
        if (networkingCheckBox.isSelected()) {
            aspects.add("Networking opportunities");
        }
        if (learningSkillsCheckBox.isSelected()) {
            aspects.add("Learning new skills");
        }
        if (careerAdviceCheckBox.isSelected()) {
            aspects.add("Career advice");
        }
        if (personalDevelopmentCheckBox.isSelected()) {
            aspects.add("Personal development");
        }
        return String.join(", ", aspects);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
