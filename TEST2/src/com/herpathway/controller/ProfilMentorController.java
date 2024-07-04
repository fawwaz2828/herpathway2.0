package com.herpathway.controller;

import javafx.stage.Stage;
import com.herpathway.model.MentorData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

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
    private Button backButton;

    @FXML
    private Button mentorshipQuestionButton; // New Button

    @FXML
    private ImageView profilePictureView;

    @FXML
    private VBox parentVBox; // Parent container for navigation

    public void setMentorData(MentorData mentor) {
        if (mentor != null) {
            displayNameLabel.setText(mentor.getDisplayName());
            fieldOfStudyLabel.setText(mentor.getFieldOfStudy());
            lastEducationLevelLabel.setText(mentor.getLastEducationLevel());
            companyNameLabel.setText("at " + mentor.getCompanyName());
            currentJobLabel.setText(mentor.getCurrentJob());
            profilePictureView.setImage(mentor.getProfilePicture());
            System.out.println("Mentor data set for: " + mentor.getDisplayName());
        } else {
            System.out.println("Mentor data is null");
        }
    }

    @FXML
    private void handleBackButtonAction() {
        // Log for debug
        System.out.println("Back button clicked.");

        // Get the HomepageController from the window's userData
        Stage stage = (Stage) backButton.getScene().getWindow();
        if (stage != null) {
            System.out.println("Stage found.");
            HomepageController homepageController = (HomepageController) stage.getUserData();
            if (homepageController != null) {
                System.out.println("HomepageController found.");
                homepageController.switchToMentorMenu();
            } else {
                System.out.println("HomepageController not found.");
            }
        } else {
            System.out.println("Stage not found.");
        }
    }

    @FXML
    private void handleMentorshipQuestionButtonAction() {
        // Log for debug
        System.out.println("Mentorship question button clicked.");

        // Get the HomepageController from the window's userData
        Stage stage = (Stage) mentorshipQuestionButton.getScene().getWindow();
        if (stage != null) {
            System.out.println("Stage found.");
            HomepageController homepageController = (HomepageController) stage.getUserData();
            if (homepageController != null) {
                System.out.println("HomepageController found.");
                homepageController.switchToMentorshipQuestion();
            } else {
                System.out.println("HomepageController not found.");
            }
        } else {
            System.out.println("Stage not found.");
        }
    }
}
