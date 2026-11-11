package com.herpathway.controller;

import com.herpathway.dao.MenteeDao;
import com.herpathway.model.MenteeProfile;
import com.herpathway.navigation.Navigator;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.FileStorage;
import com.herpathway.util.Images;
import com.herpathway.util.Ui;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/** Mentee editing their own profile (UserProfileView.fxml). */
public class UserProfileController extends AbstractMenteeFormController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label nameHeaderLabel;

    private final MenteeDao menteeDao = new MenteeDao();
    private String currentProfilePicturePath;

    @FXML
    public void initialize() {
        try {
            MenteeProfile profile = menteeDao.findByUserId(currentUserId());
            if (profile != null) {
                fillFrom(profile);
                currentProfilePicturePath = profile.getProfilePicturePath();
                nameHeaderLabel.setText(profile.getDisplayName());
            } else {
                nameHeaderLabel.setText(SessionManager.getInstance().getCurrentUserUsername());
            }
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
        }
        Ui.avatar(profileImageView, Images.fromPath(currentProfilePicturePath));
    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        String error = validate();
        if (error != null) {
            showMessage(error, true);
            return;
        }
        // Read the UI on the FX thread; only the database work runs in the background.
        MenteeProfile profile = readProfile(currentUserId());
        profile.setProfilePicturePath(currentProfilePicturePath);

        Task<Void> save = new Task<Void>() {
            @Override
            protected Void call() throws SQLException {
                menteeDao.save(profile);
                return null;
            }
        };
        save.setOnSucceeded(e -> {
            nameHeaderLabel.setText(profile.getDisplayName());
            showMessage("Profile saved.", false);
            Navigator.refreshShell();
        });
        save.setOnFailed(e -> showMessage("Couldn't save: " + save.getException().getMessage(), true));

        Thread worker = new Thread(save, "save-profile");
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    protected void handleClearAllButtonAction(ActionEvent event) {
        clearFields();
    }

    /** Picks a picture and saves it immediately. */
    @FXML
    protected void handleProfilePictureChangeAction(ActionEvent event) {
        File file = FileStorage.choose(profileImageView.getScene().getWindow(), "Choose Profile Picture", FileStorage.IMAGES);
        if (file == null) {
            return;
        }
        try {
            String path = FileStorage.store(file, FileStorage.PROFILE_PICTURES);
            menteeDao.updateProfilePicturePath(currentUserId(), path);
            currentProfilePicturePath = path;
            Ui.avatar(profileImageView, Images.fromFile(file));
            showMessage("Photo updated.", false);
            Navigator.refreshShell();
        } catch (IOException e) {
            Alerts.error("File Save Error", e);
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
        }
    }

    private static long currentUserId() {
        return SessionManager.getInstance().getCurrentUserId();
    }
}
