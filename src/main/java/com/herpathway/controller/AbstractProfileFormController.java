package com.herpathway.controller;

import com.herpathway.model.PersonalProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

/**
 * Text fields shared by the mentee and mentor profile forms
 * (MenteeDataForm, UserProfileView, MentorDataForm, MentorProfileView).
 * FXMLLoader injects these {@code @FXML} fields from the subclass's FXML.
 */
public abstract class AbstractProfileFormController {

    @FXML
    protected TextField displayNameField;
    @FXML
    protected TextField realNameField;
    @FXML
    protected TextField phoneNumberField;
    @FXML
    protected TextField emailField;
    @FXML
    protected TextField addressField;
    @FXML
    protected TextField instagramField;
    @FXML
    protected TextField linkedInField;
    @FXML
    protected TextField lastEducationLevelField;
    @FXML
    protected TextField educationalInstitutionField;
    @FXML
    protected TextField fieldOfStudyField;
    @FXML
    protected TextField graduationYearField;
    @FXML
    protected TextField currentJobField;
    @FXML
    protected TextField companyNameField;
    @FXML
    protected TextField positionField;
    @FXML
    protected TextField workExperienceField;
    @FXML
    protected Label errorLabel;

    /** Every text field on the form, including subclass-specific ones. */
    protected List<TextField> allFields() {
        return Arrays.asList(displayNameField, realNameField, phoneNumberField, emailField, addressField,
                instagramField, linkedInField, lastEducationLevelField, educationalInstitutionField, fieldOfStudyField,
                graduationYearField, currentJobField, companyNameField, positionField, workExperienceField);
    }

    /** @return an error message to show the user, or {@code null} if the form is valid */
    protected String validate() {
        for (TextField field : allFields()) {
            if (field.getText() == null || field.getText().trim().isEmpty()) {
                return "Please fill in all fields.";
            }
        }
        try {
            Integer.parseInt(graduationYearField.getText().trim());
        } catch (NumberFormatException e) {
            return "Graduation year must be a number.";
        }
        return null;
    }

    /** Copies the shared fields into {@code p}. Call {@link #validate()} first. */
    protected void readInto(PersonalProfile p) {
        p.setDisplayName(displayNameField.getText());
        p.setRealName(realNameField.getText());
        p.setPhoneNumber(phoneNumberField.getText());
        p.setEmail(emailField.getText());
        p.setAddress(addressField.getText());
        p.setInstagram(instagramField.getText());
        p.setLinkedIn(linkedInField.getText());
        p.setLastEducationLevel(lastEducationLevelField.getText());
        p.setEducationalInstitution(educationalInstitutionField.getText());
        p.setFieldOfStudy(fieldOfStudyField.getText());
        p.setGraduationYear(Integer.parseInt(graduationYearField.getText().trim()));
        p.setCurrentJob(currentJobField.getText());
        p.setCompanyName(companyNameField.getText());
        p.setPosition(positionField.getText());
        p.setWorkExperience(workExperienceField.getText());
    }

    protected void fillFrom(PersonalProfile p) {
        displayNameField.setText(p.getDisplayName());
        realNameField.setText(p.getRealName());
        phoneNumberField.setText(p.getPhoneNumber());
        emailField.setText(p.getEmail());
        addressField.setText(p.getAddress());
        instagramField.setText(p.getInstagram());
        linkedInField.setText(p.getLinkedIn());
        lastEducationLevelField.setText(p.getLastEducationLevel());
        educationalInstitutionField.setText(p.getEducationalInstitution());
        fieldOfStudyField.setText(p.getFieldOfStudy());
        graduationYearField.setText(String.valueOf(p.getGraduationYear()));
        currentJobField.setText(p.getCurrentJob());
        companyNameField.setText(p.getCompanyName());
        positionField.setText(p.getPosition());
        workExperienceField.setText(p.getWorkExperience());
    }

    /** Shows a message under the form (red for errors, green for success). */
    protected void showMessage(String message, boolean isError) {
        errorLabel.setText(message);
        errorLabel.getStyleClass().removeAll("error-label", "success-label");
        errorLabel.getStyleClass().add(isError ? "error-label" : "success-label");
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    protected void hideMessage() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
    }

    protected void clearFields() {
        allFields().forEach(TextField::clear);
    }
}
