package com.herpathway.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.herpathway.database.DatabaseConnection;
import com.herpathway.session.SessionManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MenteeDataController {

    @FXML
    private TextField displayNameField;

    @FXML
    private TextField realNameField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField instagramField;

    @FXML
    private TextField linkedInField;

    @FXML
    private TextField lastEducationLevelField;

    @FXML
    private TextField educationalInstitutionField;

    @FXML
    private TextField fieldOfStudyField;

    @FXML
    private TextField graduationYearField;

    @FXML
    private TextField currentJobField;

    @FXML
    private TextField companyNameField;

    @FXML
    private TextField positionField;

    @FXML
    private TextField workExperienceField;

    @FXML
    private TextField mainGoalField;

    @FXML
    private TextField fieldOfStudyGoalField;

    @FXML
    private TextField expectationsField;

    @FXML
    private Button profilePictureButton;

    private File profilePictureFile;

    // Default profile picture path
    private static final String DEFAULT_PROFILE_PICTURE_PATH = "profile_pictures/default_profile.png";

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        System.out.println("Submit button clicked"); // Debugging

        String displayName = displayNameField.getText();
        String realName = realNameField.getText();
        String phoneNumber = phoneNumberField.getText();
        String email = emailField.getText();
        String address = addressField.getText();
        String instagram = instagramField.getText();
        String linkedIn = linkedInField.getText();
        String lastEducationLevel = lastEducationLevelField.getText();
        String educationalInstitution = educationalInstitutionField.getText();
        String fieldOfStudy = fieldOfStudyField.getText();
        String graduationYear = graduationYearField.getText();
        String currentJob = currentJobField.getText();
        String companyName = companyNameField.getText();
        String position = positionField.getText();
        String workExperience = workExperienceField.getText();
        String mainGoal = mainGoalField.getText();
        String fieldOfStudyGoal = fieldOfStudyGoalField.getText();
        String expectations = expectationsField.getText();

        // Validate all fields are filled
        if (displayName.isEmpty() || realName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() ||
                address.isEmpty() || instagram.isEmpty() || linkedIn.isEmpty() ||
                lastEducationLevel.isEmpty() || educationalInstitution.isEmpty() || fieldOfStudy.isEmpty() ||
                graduationYear.isEmpty() || currentJob.isEmpty() || companyName.isEmpty() || position.isEmpty() ||
                workExperience.isEmpty() || mainGoal.isEmpty() || fieldOfStudyGoal.isEmpty() || expectations.isEmpty()) {
            showAlert("Form Submission Failed", "Please fill in all fields.");
            return;
        }

        // Use default profile picture if none is selected
        String profilePicturePath = (profilePictureFile == null) ? DEFAULT_PROFILE_PICTURE_PATH : saveFile(profilePictureFile, "profile_pictures");
        System.out.println("Profile picture path: " + profilePicturePath);

        // Get userId from SessionManager
        long userId = SessionManager.getInstance().getCurrentUserId();

        // Save data to database
        saveDataToDatabase(userId, displayName, realName, phoneNumber, email, address, instagram, linkedIn,
                lastEducationLevel, educationalInstitution, fieldOfStudy, Integer.parseInt(graduationYear), currentJob,
                companyName, position, workExperience, mainGoal, fieldOfStudyGoal, expectations, profilePicturePath);

        // Show success alert
        showAlert("Form Submission Successful", "Your data has been submitted successfully.");

        // Clear all fields after successful submission
        clearAllFields();

        // Navigate to Home Page
        navigateToHomePage();
    }

    @FXML
    protected void handleClearAllButtonAction(ActionEvent event) {
        clearAllFields();
    }

    @FXML
    protected void handleProfilePictureUploadAction(ActionEvent event) {
        profilePictureFile = chooseFile("Choose Profile Picture", event);
    }

    private void clearAllFields() {
        displayNameField.clear();
        realNameField.clear();
        phoneNumberField.clear();
        emailField.clear();
        addressField.clear();
        instagramField.clear();
        linkedInField.clear();
        lastEducationLevelField.clear();
        educationalInstitutionField.clear();
        fieldOfStudyField.clear();
        graduationYearField.clear();
        currentJobField.clear();
        companyNameField.clear();
        positionField.clear();
        workExperienceField.clear();
        mainGoalField.clear();
        fieldOfStudyGoalField.clear();
        expectationsField.clear();
        profilePictureFile = null;
    }

    private File chooseFile(String title, ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            showAlert("File Selected", "File: " + file.getName());
        }
        return file;
    }

    private String saveFile(File file, String directoryName) {
        try {
            Path directory = Paths.get(directoryName);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Path destination = directory.resolve(file.getName());
            Files.copy(file.toPath(), destination);
            return destination.toString();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Save Error", "Failed to save file: " + file.getName());
            return null;
        }
    }

    private void saveDataToDatabase(long userId, String displayName, String realName, String phoneNumber, String email, String address,
                                    String instagram, String linkedIn, String lastEducationLevel, String educationalInstitution,
                                    String fieldOfStudy, int graduationYear, String currentJob, String companyName,
                                    String position, String workExperience, String mainGoal, String fieldOfStudyGoal,
                                    String expectations, String profilePicturePath) {
        String sql = "INSERT INTO form_Mentee (userId, displayName, realName, phoneNumber, email, address, instagram, linkedIn, " +
                     "lastEducationLevel, educationalInstitution, fieldOfStudy, graduationYear, currentJob, companyName, position, " +
                     "workExperience, mainGoal, fieldOfStudyGoal, expectations, profilePicturePath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setString(2, displayName);
            statement.setString(3, realName);
            statement.setString(4, phoneNumber);
            statement.setString(5, email);
            statement.setString(6, address);
            statement.setString(7, instagram);
            statement.setString(8, linkedIn);
            statement.setString(9, lastEducationLevel);
            statement.setString(10, educationalInstitution);
            statement.setString(11, fieldOfStudy);
            statement.setInt(12, graduationYear);
            statement.setString(13, currentJob);
            statement.setString(14, companyName);
            statement.setString(15, position);
            statement.setString(16, workExperience);
            statement.setString(17, mainGoal);
            statement.setString(18, fieldOfStudyGoal);
            statement.setString(19, expectations);
            statement.setString(20, profilePicturePath); // Ensure this is not null

            statement.executeUpdate();
            System.out.println("Data successfully saved to the database.");
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while saving data to the database: " + e.getMessage());
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

    private void navigateToHomePage() {
        try {
            Stage stage = (Stage) displayNameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/herpathway/view/Homepage.fxml"));
            stage.setScene(new Scene(root, 967, 677));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "An error occurred while navigating to the home page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
