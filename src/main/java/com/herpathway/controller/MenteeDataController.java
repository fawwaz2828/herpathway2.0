package com.herpathway.controller;

import com.herpathway.dao.MenteeDao;
import com.herpathway.model.MenteeProfile;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.FileStorage;
import com.herpathway.util.Images;
import com.herpathway.util.Ui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/** First-login onboarding form for mentees. */
public class MenteeDataController extends AbstractMenteeFormController {

    @FXML
    private ImageView profileImageView;

    private final MenteeDao menteeDao = new MenteeDao();
    private File profilePictureFile;

    @FXML
    public void initialize() {
        Ui.avatar(profileImageView, null);
    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        String error = validate();
        if (error != null) {
            showMessage(error, true);
            return;
        }

        MenteeProfile profile = readProfile(SessionManager.getInstance().getCurrentUserId());
        try {
            // No upload -> null; Images.fromPath() then shows the bundled default picture.
            profile.setProfilePicturePath(profilePictureFile == null
                    ? null
                    : FileStorage.store(profilePictureFile, FileStorage.PROFILE_PICTURES));
            menteeDao.insert(profile);
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
        clearFields();
        hideMessage();
        profilePictureFile = null;
        Ui.avatar(profileImageView, null);
    }

    @FXML
    protected void handleProfilePictureUploadAction(ActionEvent event) {
        File file = FileStorage.choose(displayNameField.getScene().getWindow(), "Choose Profile Picture", FileStorage.IMAGES);
        if (file != null) {
            profilePictureFile = file;
            Ui.avatar(profileImageView, Images.fromFile(file));
        }
    }
}
