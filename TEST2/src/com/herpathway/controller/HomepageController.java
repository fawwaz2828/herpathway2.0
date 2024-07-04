package com.herpathway.controller;

import com.herpathway.model.MentorData;
import com.herpathway.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomepageController {

    @FXML
    private VBox contentArea;

    @FXML
    private Label profileNameLabel;

    @FXML
    private ImageView profileImageView;

    @FXML
    private void handleMentorButtonAction() {
        switchContent("MentorMenu.fxml");
    }

    @FXML
    private void handleApplicationButtonAction() {
        switchContent("application.fxml");
    }

    @FXML
    private void handleForumButtonAction() {
        switchContent("forum.fxml");
    }

    @FXML
    private void handleContentButtonAction() {
        switchContent("content.fxml");
    }

    @FXML
    private void handleProfileButtonAction() {
        switchContent("UserProfileView.fxml");
    }

    @FXML
    public void initialize() {
        // Add null check for contentArea
        if (contentArea == null) {
            System.out.println("contentArea is null.");
        } else {
            System.out.println("contentArea is not null.");
        }

        loadUserProfile();
        handleMentorButtonAction(); // Default to MentorMenu on initialize
    }

    private void switchContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/" + fxmlFile));
            Parent newLoadedPane = loader.load();

            if (fxmlFile.equals("UserProfileView.fxml")) {
                UserProfileController controller = loader.getController();
                controller.init();
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            showAlert("Load Error", "Error loading content: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void switchToMentorProfile(MentorData mentor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/ProfilMentorView.fxml"));
            Parent newLoadedPane = loader.load();

            ProfilMentorController controller = loader.getController();
            controller.setMentorData(mentor);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            showAlert("Load Error", "Error loading mentor profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void switchToMentorMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/MentorMenu.fxml"));
            Parent newLoadedPane = loader.load();
    
            // Assuming MentorMenu.fxml is controlled by MentorMenuController
            MentorMenu controller = loader.getController();
    
            contentArea.getChildren().clear();
            contentArea.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            showAlert("Load Error", "Error loading mentor menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void switchToMentorshipQuestion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/MentorshipQuestion.fxml"));
            Parent newLoadedPane = loader.load();
    
            // Assuming MentorshipQuestion.fxml is controlled by MentorshipQuestionController
            MentorshipQuestionController controller = loader.getController();
    
            // Optionally, initialize or pass data to the controller if needed
            // controller.init(); 
    
            contentArea.getChildren().clear(); // Clear existing content
            contentArea.getChildren().add(newLoadedPane); // Add new content
        } catch (IOException e) {
            showAlert("Load Error", "Error loading Mentorship Question: " + e.getMessage());
            e.printStackTrace();
        }
    }
    














    
    private MentorData fetchMentorData() {
        long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == 0) {
            showAlert("Load Error", "User ID not set.");
            return null;
        }

        String query = "SELECT displayName, fieldOfStudy, lastEducationLevel, companyName, currentJob, profilePicturePath FROM form_Mentor WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/herpath", "root", "mochimochi53");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String displayName = resultSet.getString("displayName");
                String fieldOfStudy = resultSet.getString("fieldOfStudy");
                String lastEducationLevel = resultSet.getString("lastEducationLevel");
                String companyName = resultSet.getString("companyName");
                String currentJob = resultSet.getString("currentJob");
                String profilePicturePath = resultSet.getString("profilePicturePath");

                Image profilePicture = null;
                if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                    File profilePictureFile = new File(profilePicturePath);
                    if (profilePictureFile.exists()) {
                        profilePicture = new Image(profilePictureFile.toURI().toString());
                    }
                }

                return new MentorData(displayName, fieldOfStudy, lastEducationLevel, companyName, currentJob, profilePicture);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error fetching mentor data: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void loadUserProfile() {
        long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == 0) {
            showAlert("Load Error", "User ID not set.");
            return;
        }

        String query = "SELECT displayName, profilePicturePath FROM form_Mentee WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/herpath", "root", "mochimochi53");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String displayName = resultSet.getString("displayName");
                String profilePicturePath = resultSet.getString("profilePicturePath");

                profileNameLabel.setText(displayName);

                if (profilePicturePath != null && !profilePicturePath.isEmpty()) {
                    File profilePictureFile = new File(profilePicturePath);
                    if (profilePictureFile.exists()) {
                        Image profileImage = new Image(profilePictureFile.toURI().toString());
                        profileImageView.setImage(profileImage);
                    } else {
                        profileImageView.setImage(null);
                    }
                } else {
                    profileImageView.setImage(null);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error fetching user profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
