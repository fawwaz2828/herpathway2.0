package com.herpathway.model;

/** Fields shared by the mentee and mentor profile forms (form_Mentee / form_Mentor). */
public abstract class PersonalProfile {

    private long userId;
    private String displayName;
    private String realName;
    private String phoneNumber;
    private String email;
    private String address;
    private String instagram;
    private String linkedIn;
    private String lastEducationLevel;
    private String educationalInstitution;
    private String fieldOfStudy;
    private int graduationYear;
    private String currentJob;
    private String companyName;
    private String position;
    private String workExperience;

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getLinkedIn() { return linkedIn; }
    public void setLinkedIn(String linkedIn) { this.linkedIn = linkedIn; }

    public String getLastEducationLevel() { return lastEducationLevel; }
    public void setLastEducationLevel(String lastEducationLevel) { this.lastEducationLevel = lastEducationLevel; }

    public String getEducationalInstitution() { return educationalInstitution; }
    public void setEducationalInstitution(String educationalInstitution) { this.educationalInstitution = educationalInstitution; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public int getGraduationYear() { return graduationYear; }
    public void setGraduationYear(int graduationYear) { this.graduationYear = graduationYear; }

    public String getCurrentJob() { return currentJob; }
    public void setCurrentJob(String currentJob) { this.currentJob = currentJob; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getWorkExperience() { return workExperience; }
    public void setWorkExperience(String workExperience) { this.workExperience = workExperience; }
}
