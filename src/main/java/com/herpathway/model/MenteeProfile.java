package com.herpathway.model;

/** A row of form_Mentee. */
public class MenteeProfile extends PersonalProfile {

    /** Value of {@code form_Mentee.status} once a mentor accepts the mentee. */
    public static final String STATUS_ACCEPTED = "Accepted";

    private String mainGoal;
    private String fieldOfStudyGoal;
    private String expectations;
    private String profilePicturePath;

    public String getMainGoal() { return mainGoal; }
    public void setMainGoal(String mainGoal) { this.mainGoal = mainGoal; }

    public String getFieldOfStudyGoal() { return fieldOfStudyGoal; }
    public void setFieldOfStudyGoal(String fieldOfStudyGoal) { this.fieldOfStudyGoal = fieldOfStudyGoal; }

    public String getExpectations() { return expectations; }
    public void setExpectations(String expectations) { this.expectations = expectations; }

    public String getProfilePicturePath() { return profilePicturePath; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }
}
