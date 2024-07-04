package com.herpathway.model;

public class ProfilMentor {
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
    private String graduationYear;
    private String currentJob;
    private String companyName;
    private String position;
    private String workExperience;

    // Constructor
    public ProfilMentor(String displayName, String realName, String phoneNumber, String email, String address, 
                        String instagram, String linkedIn, String lastEducationLevel, String educationalInstitution, 
                        String fieldOfStudy, String graduationYear, String currentJob, String companyName, 
                        String position, String workExperience) {
        this.displayName = displayName;
        this.realName = realName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.instagram = instagram;
        this.linkedIn = linkedIn;
        this.lastEducationLevel = lastEducationLevel;
        this.educationalInstitution = educationalInstitution;
        this.fieldOfStudy = fieldOfStudy;
        this.graduationYear = graduationYear;
        this.currentJob = currentJob;
        this.companyName = companyName;
        this.position = position;
        this.workExperience = workExperience;
    }

    // Getters and setters for all attributes
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getLastEducationLevel() {
        return lastEducationLevel;
    }

    public void setLastEducationLevel(String lastEducationLevel) {
        this.lastEducationLevel = lastEducationLevel;
    }

    public String getEducationalInstitution() {
        return educationalInstitution;
    }

    public void setEducationalInstitution(String educationalInstitution) {
        this.educationalInstitution = educationalInstitution;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(String currentJob) {
        this.currentJob = currentJob;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getWorkExperience() {
        return workExperience;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    // Method to display mentor data
    public String displayData() {
        return "Display Name: " + displayName + "\n" +
                "Real Name: " + realName + "\n" +
                "Phone Number: " + phoneNumber + "\n" +
                "Email: " + email + "\n" +
                "Address: " + address + "\n" +
                "Instagram: " + instagram + "\n" +
                "LinkedIn: " + linkedIn + "\n" +
                "Last Education Level: " + lastEducationLevel + "\n" +
                "Educational Institution: " + educationalInstitution + "\n" +
                "Field of Study: " + fieldOfStudy + "\n" +
                "Graduation Year: " + graduationYear + "\n" +
                "Current Job: " + currentJob + "\n" +
                "Company Name: " + companyName + "\n" +
                "Position: " + position + "\n" +
                "Work Experience: " + workExperience;
    }

    public byte[] getProfilePictureData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProfilePictureData'");
    }
}
