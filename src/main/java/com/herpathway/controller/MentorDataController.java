package com.herpathway.controller;

import com.herpathway.dao.MentorDao;
import com.herpathway.model.MentorProfile;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.FileStorage;
import com.herpathway.util.ImageCompressor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Consumer;

/** First-login onboarding form for mentors. Documents are stored in the database as BLOBs. */
public class MentorDataController extends AbstractProfileFormController {

    private static final String NO_FILE = "No file chosen";

    @FXML
    private Label profilePictureNameLabel;
    @FXML
    private Label idCardNameLabel;
    @FXML
    private Label cvNameLabel;
    @FXML
    private Label certificateNameLabel;

    private final MentorDao mentorDao = new MentorDao();

    private byte[] profilePicture;
    private byte[] idCard;
    private byte[] cv;
    private byte[] certificate;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        String error = validate();
        if (error != null) {
            showMessage(error, true);
            return;
        }
        if (profilePicture == null || idCard == null || cv == null || certificate == null) {
            showMessage("Upload your profile photo, ID card, CV and certificate.", true);
            return;
        }

        MentorProfile profile = new MentorProfile();
        readInto(profile);
        profile.setUserId(SessionManager.getInstance().getCurrentUserId());
        profile.setProfilePicture(profilePicture);
        profile.setIdCard(idCard);
        profile.setCv(cv);
        profile.setCertificate(certificate);

        try {
            mentorDao.insert(profile);
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        Navigator.goTo(View.HOME);
    }

    @FXML
    protected void handleClearAllButtonAction(ActionEvent event) {
        clearFields();
        hideMessage();
        profilePicture = idCard = cv = certificate = null;
        for (Label l : new Label[]{profilePictureNameLabel, idCardNameLabel, cvNameLabel, certificateNameLabel}) {
            l.setText(NO_FILE);
        }
    }

    @FXML
    protected void handleProfilePictureUploadAction(ActionEvent event) {
        pickImage("Choose Profile Picture", profilePictureNameLabel, data -> profilePicture = data);
    }

    @FXML
    protected void handleIdCardUploadAction(ActionEvent event) {
        pickImage("Choose ID Card", idCardNameLabel, data -> idCard = data);
    }

    @FXML
    protected void handleCvUploadAction(ActionEvent event) {
        pickImage("Choose CV", cvNameLabel, data -> cv = data);
    }

    @FXML
    protected void handleCertificateUploadAction(ActionEvent event) {
        pickImage("Choose Certificate", certificateNameLabel, data -> certificate = data);
    }

    /** Lets the user pick an image, compresses it if needed and shows the file name next to the button. */
    private void pickImage(String title, Label nameLabel, Consumer<byte[]> target) {
        File file = FileStorage.choose(displayNameField.getScene().getWindow(), title, FileStorage.IMAGES);
        if (file == null) {
            return;
        }
        try {
            target.accept(ImageCompressor.readWithinLimit(file));
            nameLabel.setText("✓ " + file.getName());
            hideMessage();
        } catch (IOException e) {
            showMessage(file.getName() + ": " + e.getMessage(), true);
        }
    }
}
