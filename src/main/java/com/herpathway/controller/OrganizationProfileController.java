package com.herpathway.controller;

import com.herpathway.dao.OrganizationDao;
import com.herpathway.model.OrganizationProfile;
import com.herpathway.navigation.Navigator;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

/** Lets an organization edit the details it entered during onboarding. */
public class OrganizationProfileController {

    @FXML private TextField organizationNameField;
    @FXML private TextField organizationRegistrationNumberField;
    @FXML private TextField descriptionField;
    @FXML private TextField visionMisionField;
    @FXML private TextField serviceField;
    @FXML private TextField targetField;
    @FXML private TextField methodologyField;
    @FXML private TextField programOutcomesField;
    @FXML private TextField contactField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private TextField websiteField;
    @FXML private TextField addressField;
    @FXML private Label messageLabel;

    private final OrganizationDao organizationDao = new OrganizationDao();
    private OrganizationProfile profile;

    @FXML
    public void initialize() {
        try {
            profile = organizationDao.findByUserId(SessionManager.getInstance().getCurrentUserId());
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        if (profile == null) {
            return;
        }
        organizationNameField.setText(profile.getOrganizationName());
        organizationRegistrationNumberField.setText(profile.getOrganizationRegistrationNumber());
        descriptionField.setText(profile.getDescription());
        visionMisionField.setText(profile.getVisionMision());
        serviceField.setText(profile.getService());
        targetField.setText(profile.getTarget());
        methodologyField.setText(profile.getMethodology());
        programOutcomesField.setText(profile.getProgramOutcomes());
        contactField.setText(profile.getContact());
        phoneNumberField.setText(profile.getPhoneNumber());
        emailField.setText(profile.getEmail());
        websiteField.setText(profile.getWebsite());
        addressField.setText(profile.getAddress());
    }

    @FXML
    private void handleSave() {
        if (profile == null) {
            showMessage("Your organization profile wasn't found. Log in again to complete it.", true);
            return;
        }
        if (Ui.isBlank(organizationNameField.getText())) {
            showMessage("The organization name can't be empty.", true);
            return;
        }
        profile.setOrganizationName(organizationNameField.getText().trim());
        profile.setOrganizationRegistrationNumber(organizationRegistrationNumberField.getText());
        profile.setDescription(descriptionField.getText());
        profile.setVisionMision(visionMisionField.getText());
        profile.setService(serviceField.getText());
        profile.setTarget(targetField.getText());
        profile.setMethodology(methodologyField.getText());
        profile.setProgramOutcomes(programOutcomesField.getText());
        profile.setContact(contactField.getText());
        profile.setPhoneNumber(phoneNumberField.getText());
        profile.setEmail(emailField.getText());
        profile.setWebsite(websiteField.getText());
        profile.setAddress(addressField.getText());
        try {
            organizationDao.update(profile);
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Couldn't save: " + e.getMessage(), true);
            return;
        }
        showMessage("Profile saved.", false);
        Navigator.refreshShell();
    }

    private void showMessage(String text, boolean isError) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("error-label", "success-label");
        messageLabel.getStyleClass().add(isError ? "error-label" : "success-label");
        Ui.setShown(messageLabel, true);
    }
}
