package com.herpathway.controller;

import com.herpathway.dao.OrganizationDao;
import com.herpathway.model.OrganizationProfile;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.FileStorage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/** First-login onboarding form for organizations. */
public class OrganizationDataController {

    @FXML
    private TextField organizationNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField visionMisionField;
    @FXML
    private TextField serviceField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField websiteField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField targetField;
    @FXML
    private TextField methodologyField;
    @FXML
    private TextField programOutcomesField;
    @FXML
    private TextField organizationRegistrationNumberField;
    @FXML
    private Label foundingDocumentNameLabel;
    @FXML
    private Label financialReportNameLabel;
    @FXML
    private Label errorLabel;

    private final OrganizationDao organizationDao = new OrganizationDao();
    private File foundingDocumentFile;
    private File financialReportsFile;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        for (TextField field : allFields()) {
            if (field.getText() == null || field.getText().trim().isEmpty()) {
                showError("Please fill in all fields.");
                return;
            }
        }
        if (foundingDocumentFile == null || financialReportsFile == null) {
            showError("Upload the founding documents and financial reports.");
            return;
        }

        OrganizationProfile o = new OrganizationProfile();
        o.setUserId(SessionManager.getInstance().getCurrentUserId());
        o.setOrganizationName(organizationNameField.getText());
        o.setAddress(addressField.getText());
        o.setVisionMision(visionMisionField.getText());
        o.setService(serviceField.getText());
        o.setContact(contactField.getText());
        o.setPhoneNumber(phoneNumberField.getText());
        o.setEmail(emailField.getText());
        o.setWebsite(websiteField.getText());
        o.setDescription(descriptionField.getText());
        o.setTarget(targetField.getText());
        o.setMethodology(methodologyField.getText());
        o.setProgramOutcomes(programOutcomesField.getText());
        o.setOrganizationRegistrationNumber(organizationRegistrationNumberField.getText());

        try {
            o.setFoundingDocumentPath(FileStorage.store(foundingDocumentFile, FileStorage.FOUNDING_DOCUMENTS));
            o.setFinancialReportPath(FileStorage.store(financialReportsFile, FileStorage.FINANCIAL_REPORTS));
            organizationDao.insert(o);
        } catch (IOException e) {
            Alerts.error("File Save Error", e);
            return;
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }

        Navigator.goTo(View.HOME);
    }

    @FXML
    protected void handleClearAllButtonAction(ActionEvent event) {
        clearAll();
    }

    @FXML
    protected void handleFoundingDocumentsAction(ActionEvent event) {
        File file = chooseDocument("Choose Founding Document");
        if (file != null) {
            foundingDocumentFile = file;
            foundingDocumentNameLabel.setText("✓ " + file.getName());
        }
    }

    @FXML
    protected void handleFinancialReportsAction(ActionEvent event) {
        File file = chooseDocument("Choose Financial Reports");
        if (file != null) {
            financialReportsFile = file;
            financialReportNameLabel.setText("✓ " + file.getName());
        }
    }

    private File chooseDocument(String title) {
        return FileStorage.choose(organizationNameField.getScene().getWindow(), title, FileStorage.PDF, FileStorage.IMAGES);
    }

    private List<TextField> allFields() {
        return Arrays.asList(organizationNameField, addressField, visionMisionField, serviceField, contactField,
                phoneNumberField, emailField, websiteField, descriptionField, targetField, methodologyField,
                programOutcomesField, organizationRegistrationNumberField);
    }

    private void clearAll() {
        allFields().forEach(TextField::clear);
        foundingDocumentFile = null;
        financialReportsFile = null;
        foundingDocumentNameLabel.setText("No file chosen");
        financialReportNameLabel.setText("No file chosen");
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
