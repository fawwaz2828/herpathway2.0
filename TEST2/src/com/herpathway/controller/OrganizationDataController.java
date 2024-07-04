package com.herpathway.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.herpathway.database.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    private File foundingDocumentFile;
    private File financialReportsFile;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            // Debugging log
            System.out.println("Submit button clicked");
            
            String organizationName = organizationNameField.getText();
            String address = addressField.getText();
            String visionMision = visionMisionField.getText();
            String service = serviceField.getText();
            String contact = contactField.getText();
            String phoneNumber = phoneNumberField.getText();
            String email = emailField.getText();
            String website = websiteField.getText();
            String description = descriptionField.getText();
            String target = targetField.getText();
            String methodology = methodologyField.getText();
            String programOutcomes = programOutcomesField.getText();
            String organizationRegistrationNumber = organizationRegistrationNumberField.getText();

            // Debugging statements
            System.out.println("Organization Name: " + organizationName);
            System.out.println("Address: " + address);
            System.out.println("Vision & Mission: " + visionMision);
            System.out.println("Service: " + service);
            System.out.println("Contact: " + contact);
            System.out.println("Phone Number: " + phoneNumber);
            System.out.println("Email: " + email);
            System.out.println("Website: " + website);
            System.out.println("Description: " + description);
            System.out.println("Target: " + target);
            System.out.println("Methodology: " + methodology);
            System.out.println("Program Outcomes: " + programOutcomes);
            System.out.println("Organization Registration Number: " + organizationRegistrationNumber);

            // Validasi data
            if (organizationName == null || address == null || visionMision == null || service == null ||
                    contact == null || phoneNumber == null || email == null ||
                    website == null || description == null || target == null || methodology == null ||
                    programOutcomes == null || organizationRegistrationNumber == null ||
                    organizationName.isEmpty() || address.isEmpty() || visionMision.isEmpty() || service.isEmpty() ||
                    contact.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() ||
                    website.isEmpty() || description.isEmpty() || target.isEmpty() || methodology.isEmpty() ||
                    programOutcomes.isEmpty() || organizationRegistrationNumber.isEmpty()) {
                showAlert("Form Submission Failed", "Please fill in all fields.");
                return;
            }

            if (foundingDocumentFile == null || financialReportsFile == null) {
                showAlert("Form Submission Failed", "Please upload all required files.");
                return;
            }

            String foundingDocumentPath = saveFile(foundingDocumentFile, "founding_documents");
            String financialReportPath = saveFile(financialReportsFile, "financial_reports");

            // Get userId (mocked implementation, replace with actual logic)
            long userId = getUserIdFromSession();

            // Save data to database
            saveDataToDatabase(userId, organizationName, address, visionMision, service, contact, phoneNumber, email, website,
                    description, target, methodology, programOutcomes, organizationRegistrationNumber, foundingDocumentPath, financialReportPath);

            showAlert("Form Submission Successful", "Your data has been submitted successfully.");
            clearAllFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void handleClearAllButtonAction(ActionEvent event) {
        clearAllFields();
    }

    private void clearAllFields() {
        organizationNameField.clear();
        addressField.clear();
        visionMisionField.clear();
        serviceField.clear();
        contactField.clear();
        phoneNumberField.clear();
        emailField.clear();
        websiteField.clear();
        descriptionField.clear();
        targetField.clear();
        methodologyField.clear();
        programOutcomesField.clear();
        organizationRegistrationNumberField.clear();
        foundingDocumentFile = null;
        financialReportsFile = null;
    }

    @FXML
    protected void handleFoundingDocumentsAction(ActionEvent event) {
        foundingDocumentFile = chooseFile("Choose Founding Document", event);
    }

    @FXML
    protected void handleFinancialReportsAction(ActionEvent event) {
        financialReportsFile = chooseFile("Choose Financial Reports", event);
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

    private void saveDataToDatabase(long userId, String organizationName, String address, String visionMision, String service,
                                    String contact, String phoneNumber, String email, String website, String description,
                                    String target, String methodology, String programOutcomes, String organizationRegistrationNumber,
                                    String foundingDocumentPath, String financialReportPath) {
        String sql = "INSERT INTO form_Organization (userId, organizationName, address, visionMision, service, contact, phoneNumber, " +
                "email, website, description, target, methodology, programOutcomes, organizationRegistrationNumber, " +
                "foundingDocumentPath, financialReportPath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);
            statement.setString(2, organizationName);
            statement.setString(3, address);
            statement.setString(4, visionMision);
            statement.setString(5, service);
            statement.setString(6, contact);
            statement.setString(7, phoneNumber);
            statement.setString(8, email);
            statement.setString(9, website);
            statement.setString(10, description);
            statement.setString(11, target);
            statement.setString(12, methodology);
            statement.setString(13, programOutcomes);
            statement.setString(14, organizationRegistrationNumber);
            statement.setString(15, foundingDocumentPath);
            statement.setString(16, financialReportPath);

            statement.executeUpdate();
            System.out.println("Data successfully saved to the database.");
        } catch (SQLException e) {
            showAlert("Database Error", "An error occurred while saving data to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private long getUserIdFromSession() {
        // Mocked implementation for demonstration, replace with actual logic to get userId from session
        return 1; // Replace with actual userId retrieval logic
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


