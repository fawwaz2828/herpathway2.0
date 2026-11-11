package com.herpathway.controller;

import com.herpathway.dao.MentorDao;
import com.herpathway.model.MentorProfile;
import com.herpathway.navigation.Navigator;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Images;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.sql.SQLException;

/** Mentor editing their own profile (MentorProfileView.fxml). */
public class MentorProfileController extends AbstractProfileFormController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label nameHeaderLabel;

    private final MentorDao mentorDao = new MentorDao();

    @FXML
    private void initialize() {
        MentorProfile profile = null;
        try {
            profile = mentorDao.findByUserId(currentUserId());
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
        }
        if (profile != null) {
            fillFrom(profile);
            nameHeaderLabel.setText(profile.getDisplayName());
        }
        Ui.avatar(profileImageView, Images.fromBytes(profile != null ? profile.getProfilePicture() : null));
    }

    @FXML
    private void handleSaveButtonAction() {
        String error = validate();
        if (error != null) {
            showMessage(error, true);
            return;
        }
        MentorProfile profile = new MentorProfile();
        readInto(profile);
        profile.setUserId(currentUserId());
        try {
            mentorDao.updateDetails(profile);
        } catch (SQLException e) {
            showMessage("Couldn't save: " + e.getMessage(), true);
            return;
        }
        nameHeaderLabel.setText(profile.getDisplayName());
        showMessage("Profile saved.", false);
        Navigator.refreshShell();
    }

    private static long currentUserId() {
        return SessionManager.getInstance().getCurrentUserId();
    }
}
