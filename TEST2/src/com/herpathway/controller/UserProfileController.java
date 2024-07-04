package com.herpathway.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserProfileController {

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

    @FXML
    private ImageView profileImageView; // Add an ImageView for the profile picture

    private File profilePictureFile;

    // Default profile picture path

    private String currentProfilePicturePath; // Track the current profile picture path

    public void init() {
        loadUserProfileData();
    }

    private void loadUserProfileData() {
        long userId = SessionManager.getInstance().getCurrentUserId();

        String sql = "SELECT * FROM form_Mentee WHERE userId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                displayNameField.setText(resultSet.getString("displayName"));
                realNameField.setText(resultSet.getString("realName"));
                phoneNumberField.setText(resultSet.getString("phoneNumber"));
                emailField.setText(resultSet.getString("email"));
                addressField.setText(resultSet.getString("address"));
                instagramField.setText(resultSet.getString("instagram"));
                linkedInField.setText(resultSet.getString("linkedIn"));
                lastEducationLevelField.setText(resultSet.getString("lastEducationLevel"));
                educationalInstitutionField.setText(resultSet.getString("educationalInstitution"));
                fieldOfStudyField.setText(resultSet.getString("fieldOfStudy"));
                graduationYearField.setText(String.valueOf(resultSet.getInt("graduationYear")));
                currentJobField.setText(resultSet.getString("currentJob"));
                companyNameField.setText(resultSet.getString("companyName"));
                positionField.setText(resultSet.getString("position"));
                workExperienceField.setText(resultSet.getString("workExperience"));
                mainGoalField.setText(resultSet.getString("mainGoal"));
                fieldOfStudyGoalField.setText(resultSet.getString("fieldOfStudyGoal"));
                expectationsField.setText(resultSet.getString("expectations"));

                currentProfilePicturePath = resultSet.getString("profilePicturePath");
                if (currentProfilePicturePath != null && !currentProfilePicturePath.isEmpty()) {
                    File profilePictureFile = new File(currentProfilePicturePath);
                    if (profilePictureFile.exists()) {
                        Image profileImage = new Image(profilePictureFile.toURI().toString());
                        profileImageView.setImage(profileImage);
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while fetching data from the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
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

        // Use existing profile picture if none is selected
        String profilePicturePath = (profilePictureFile == null) ? currentProfilePicturePath : saveFile(profilePictureFile, "profile_pictures");

        // Get userId from SessionManager
        long userId = SessionManager.getInstance().getCurrentUserId();

        // Save data to database
        if (isUserProfileExist(userId)) {
            updateDataToDatabase(userId, displayName, realName, phoneNumber, email, address, instagram, linkedIn,
                    lastEducationLevel, educationalInstitution, fieldOfStudy, Integer.parseInt(graduationYear), currentJob,
                    companyName, position, workExperience, mainGoal, fieldOfStudyGoal, expectations, profilePicturePath);
        } else {
            saveDataToDatabase(userId, displayName, realName, phoneNumber, email, address, instagram, linkedIn,
                    lastEducationLevel, educationalInstitution, fieldOfStudy, Integer.parseInt(graduationYear), currentJob,
                    companyName, position, workExperience, mainGoal, fieldOfStudyGoal, expectations, profilePicturePath);
        }

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
        if (profilePictureFile != null) {
            Image profileImage = new Image(profilePictureFile.toURI().toString());
            profileImageView.setImage(profileImage);
        }
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
        profileImageView.setImage(null); // Clear the profile image
        profilePictureFile = null;
        currentProfilePicturePath = null;
    }

    private File chooseFile(String title, ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
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
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String fileName = timestamp + "_" + file.getName();
            Path destination = directory.resolve(fileName);
            Files.copy(file.toPath(), destination);
            return destination.toString();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Save Error", "Failed to save file: " + file.getName());
            return null;
        }
    }

    private boolean isUserProfileExist(long userId) {
        String sql = "SELECT COUNT(*) FROM form_Mentee WHERE userId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while checking user profile: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
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

    private void updateDataToDatabase(long userId, String displayName, String realName, String phoneNumber, String email, String address,
                                      String instagram, String linkedIn, String lastEducationLevel, String educationalInstitution,
                                      String fieldOfStudy, int graduationYear, String currentJob, String companyName,
                                      String position, String workExperience, String mainGoal, String fieldOfStudyGoal,
                                      String expectations, String profilePicturePath) {
        String sql = "UPDATE form_Mentee SET displayName = ?, realName = ?, phoneNumber = ?, email = ?, address = ?, instagram = ?, " +
                     "linkedIn = ?, lastEducationLevel = ?, educationalInstitution = ?, fieldOfStudy = ?, graduationYear = ?, currentJob = ?, " +
                     "companyName = ?, position = ?, workExperience = ?, mainGoal = ?, fieldOfStudyGoal = ?, expectations = ?, profilePicturePath = ? " +
                     "WHERE userId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, displayName);
            statement.setString(2, realName);
            statement.setString(3, phoneNumber);
            statement.setString(4, email);
            statement.setString(5, address);
            statement.setString(6, instagram);
            statement.setString(7, linkedIn);
            statement.setString(8, lastEducationLevel);
            statement.setString(9, educationalInstitution);
            statement.setString(10, fieldOfStudy);
            statement.setInt(11, graduationYear);
            statement.setString(12, currentJob);
            statement.setString(13, companyName);
            statement.setString(14, position);
            statement.setString(15, workExperience);
            statement.setString(16, mainGoal);
            statement.setString(17, fieldOfStudyGoal);
            statement.setString(18, expectations);
            statement.setString(19, profilePicturePath); // Ensure this is not null
            statement.setLong(20, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while updating data to the database: " + e.getMessage());
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

    @FXML
    protected void handleProfilePictureChangeAction(ActionEvent event) {
        profilePictureFile = chooseFile("Choose Profile Picture", event);
        if (profilePictureFile == null) {
            showAlert("Profile Picture Change Failed", "Please select a profile picture to upload.");
            return;
        }

        String profilePicturePath = saveFile(profilePictureFile, "profile_pictures");
        if (profilePicturePath == null) {
            showAlert("Profile Picture Change Failed", "Failed to save the selected profile picture.");
            return;
        }

        long userId = SessionManager.getInstance().getCurrentUserId();
        String sql = "UPDATE form_Mentee SET profilePicturePath = ? WHERE userId = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, profilePicturePath);
            statement.setLong(2, userId);

            statement.executeUpdate();

            showAlert("Profile Picture Change Successful", "Your profile picture has been updated successfully.");

            // Update the profile image view with the new profile picture
            Image profileImage = new Image(profilePictureFile.toURI().toString());
            profileImageView.setImage(profileImage);
            currentProfilePicturePath = profilePicturePath; // Update the current profile picture path

        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while updating the profile picture in the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
