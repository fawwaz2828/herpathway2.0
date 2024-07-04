package com.herpathway.controller;

import javafx.embed.swing.SwingFXUtils;
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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.session.SessionManager;

public class MentorDataController {

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

    private File profilePictureFile;
    private File idCardFile;
    private File cvFile;
    private File certificateFile;

    private static final int MAX_IMAGE_SIZE = 1048576; // 1 MB

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

        // Validate all fields are filled
        if (displayName.isEmpty() || realName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() ||
                address.isEmpty() || instagram.isEmpty() || linkedIn.isEmpty() ||
                lastEducationLevel.isEmpty() || educationalInstitution.isEmpty() || fieldOfStudy.isEmpty() ||
                graduationYear.isEmpty() || currentJob.isEmpty() || companyName.isEmpty() || position.isEmpty() ||
                workExperience.isEmpty()) {
            showAlert("Form Submission Failed", "Please fill in all fields.");
            return;
        }

        // Read files to byte arrays
        byte[] profilePictureData = readFileToByteArray(profilePictureFile);
        byte[] idCardData = readFileToByteArray(idCardFile);
        byte[] cvData = readFileToByteArray(cvFile);
        byte[] certificateData = readFileToByteArray(certificateFile);

        // Check if any file reading failed
        if (profilePictureData == null || idCardData == null || cvData == null || certificateData == null) {
            showAlert("Form Submission Failed", "One or more files could not be read. Please try again.");
            return;
        }

        // Get userId from SessionManager
        long userId = SessionManager.getInstance().getCurrentUserId();

        // Save data to database
        saveDataToDatabase(userId, displayName, realName, phoneNumber, email, address, instagram, linkedIn,
                lastEducationLevel, educationalInstitution, fieldOfStudy, Integer.parseInt(graduationYear), currentJob,
                companyName, position, workExperience, profilePictureData, idCardData, cvData, certificateData);

        // Show success alert
        showAlert("Form Submission Successful", "Your data has been submitted successfully.");

        // Clear all fields after successful submission
        clearAllFields();

        // Navigate to Mentor Homepage
        navigateToMentorHomepage();
    }

    @FXML
    protected void handleClearAllButtonAction(ActionEvent event) {
        clearAllFields();
    }

    @FXML
    protected void handleProfilePictureUploadAction(ActionEvent event) {
        profilePictureFile = chooseFile("Choose Profile Picture", event, new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        profilePictureFile = processImageFile(profilePictureFile);
    }

    @FXML
    protected void handleIdCardUploadAction(ActionEvent event) {
        idCardFile = chooseFile("Choose ID Card", event, new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        idCardFile = processImageFile(idCardFile);
    }

    @FXML
    protected void handleCvUploadAction(ActionEvent event) {
        cvFile = chooseFile("Choose CV", event, new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        cvFile = processImageFile(cvFile);
    }

    @FXML
    protected void handleCertificateUploadAction(ActionEvent event) {
        certificateFile = chooseFile("Choose Certificate", event, new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        certificateFile = processImageFile(certificateFile);
    }

    private File processImageFile(File imageFile) {
        if (imageFile != null) {
            // Check the file size and compress if necessary
            byte[] imageData = readFileToByteArray(imageFile);
            if (imageData != null && imageData.length > MAX_IMAGE_SIZE) {
                try {
                    imageData = compressImage(imageFile);
                    if (imageData.length > MAX_IMAGE_SIZE) {
                        showAlert("File Too Large", "The selected file exceeds the allowable size limit even after compression.");
                        return null; // reset the file selection
                    } else {
                        showAlert("File Selected", "File selected and compressed successfully.");
                    }
                } catch (IOException e) {
                    showAlert("Compression Error", "An error occurred while compressing the file: " + e.getMessage());
                    return null; // reset the file selection
                }
            }
        }
        return imageFile;
    }

    private byte[] compressImage(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        baos.flush();
        byte[] compressedImageData = baos.toByteArray();
        baos.close();
        return compressedImageData;
    }

    private byte[] readFileToByteArray(File file) {
        if (file == null) {
            return null;
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("File Read Error", "Failed to read file: " + file.getName());
            return null;
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
        profilePictureFile = null;
        idCardFile = null;
        cvFile = null;
        certificateFile = null;
    }

    private File chooseFile(String title, ActionEvent event, FileChooser.ExtensionFilter filter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(filter);
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            showAlert("File Selected", "File: " + file.getName());
        }
        return file;
    }

    private void saveDataToDatabase(long userId, String displayName, String realName, String phoneNumber, String email, String address,
                                    String instagram, String linkedIn, String lastEducationLevel, String educationalInstitution,
                                    String fieldOfStudy, int graduationYear, String currentJob, String companyName,
                                    String position, String workExperience, byte[] profilePictureData, byte[] idCardData,
                                    byte[] cvData, byte[] certificateData) {
        String sql = "INSERT INTO form_Mentor (userId, displayName, realName, phoneNumber, email, address, instagram, linkedIn, " +
                     "lastEducationLevel, educationalInstitution, fieldOfStudy, graduationYear, currentJob, companyName, position, " +
                     "workExperience, profilePicture, idCard, cv, certificate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            statement.setBytes(17, profilePictureData);
            statement.setBytes(18, idCardData);
            statement.setBytes(19, cvData);
            statement.setBytes(20, certificateData);

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

    private void navigateToMentorHomepage() {
        try {
            Stage stage = (Stage) displayNameField.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/com/herpathway/view/MentorHomepage.fxml"));
            stage.setScene(new Scene(root, 967, 677));
            stage.show();
        } catch (IOException e) {
            showAlert("Navigation Error", "An error occurred while navigating to the mentor home page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
