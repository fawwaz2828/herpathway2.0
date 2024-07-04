package com.herpathway.model;

import javafx.scene.image.Image;

public class MentorData {
    private String displayName;
    private String fieldOfStudy;
    private String lastEducationLevel;
    private String companyName;
    private String currentJob;
    private Image profilePicture;

    public MentorData(String displayName, String fieldOfStudy, String lastEducationLevel, String companyName, String currentJob, Image profilePicture) {
        this.displayName = displayName;
        this.fieldOfStudy = fieldOfStudy;
        this.lastEducationLevel = lastEducationLevel;
        this.companyName = companyName;
        this.currentJob = currentJob;
        this.profilePicture = profilePicture;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public String getLastEducationLevel() {
        return lastEducationLevel;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCurrentJob() {
        return currentJob;
    }

    public Image getProfilePicture() {
        return profilePicture;
    }
}
