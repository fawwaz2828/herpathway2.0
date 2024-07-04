package com.herpathway.controller;

import com.herpathway.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MentorHomepageController {

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
        switchContent("ApplicationMenu.fxml");
    }

    @FXML
    private void handleForumButtonAction() {
        switchContent("ForumMenu.fxml");
    }

    @FXML
    private void handleContentButtonAction() {
        switchContent("ContentMenu.fxml");
    }

    @FXML
    private void handleProfileButtonAction() {
        switchContent("MentorProfileView.fxml");
    }

    @FXML
    public void initialize() {
        loadUserProfile();
    }

    private void switchContent(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/herpathway/view/" + fxmlFile));
            Parent newLoadedPane = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            showAlert("Load Error", "An error occurred while loading the content: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUserProfile() {
        long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == 0) {
            showAlert("Load Error", "User ID is not set.");
            return;
        }
        
        String query = "SELECT displayName, profilePicture FROM form_Mentor WHERE userId = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/herpath", "root", "mochimochi53");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
             
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String displayName = resultSet.getString("displayName");
                byte[] profilePictureData = resultSet.getBytes("profilePicture");

                profileNameLabel.setText(displayName);
                
                if (profilePictureData != null) {
                    Image profileImage = new Image(new ByteArrayInputStream(profilePictureData));
                    profileImageView.setImage(profileImage);
                    System.out.println("Profile image loaded successfully");
                } else {
                    showAlert("Load Error", "Profile picture data is not found in the database.");
                    System.out.println("Profile picture data is null");
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while fetching the user profile: " + e.getMessage());
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
