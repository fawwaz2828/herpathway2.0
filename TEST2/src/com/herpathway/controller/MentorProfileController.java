// ProfileController.java
package com.herpathway.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import com.herpathway.database.DatabaseConnection;
import com.herpathway.session.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MentorProfileController {

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
    private void initialize() {
        loadData();
    }

    private void loadData() {
        long userId = SessionManager.getInstance().getCurrentUserId();

        String sql = "SELECT * FROM form_Mentor WHERE userId = ?";
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
                graduationYearField.setText(resultSet.getString("graduationYear"));
                currentJobField.setText(resultSet.getString("currentJob"));
                companyNameField.setText(resultSet.getString("companyName"));
                positionField.setText(resultSet.getString("position"));
                workExperienceField.setText(resultSet.getString("workExperience"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveButtonAction() {
        long userId = SessionManager.getInstance().getCurrentUserId();
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

        String sql = "UPDATE form_Mentor SET displayName = ?, realName = ?, phoneNumber = ?, email = ?, address = ?, instagram = ?, linkedIn = ?, " +
                "lastEducationLevel = ?, educationalInstitution = ?, fieldOfStudy = ?, graduationYear = ?, currentJob = ?, companyName = ?, " +
                "position = ?, workExperience = ? WHERE userId = ?";

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
            statement.setString(11, graduationYear);
            statement.setString(12, currentJob);
            statement.setString(13, companyName);
            statement.setString(14, position);
            statement.setString(15, workExperience);
            statement.setLong(16, userId);

            statement.executeUpdate();
            showAlert("Update Successful", "Your profile has been updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Update Failed", "An error occurred while updating your profile.");
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
