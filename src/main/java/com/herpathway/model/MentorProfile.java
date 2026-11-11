package com.herpathway.model;

/** A row of form_Mentor. Documents are stored as BLOBs. */
public class MentorProfile extends PersonalProfile {

    private byte[] profilePicture;
    private byte[] idCard;
    private byte[] cv;
    private byte[] certificate;

    public byte[] getProfilePicture() { return profilePicture; }
    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }

    public byte[] getIdCard() { return idCard; }
    public void setIdCard(byte[] idCard) { this.idCard = idCard; }

    public byte[] getCv() { return cv; }
    public void setCv(byte[] cv) { this.cv = cv; }

    public byte[] getCertificate() { return certificate; }
    public void setCertificate(byte[] certificate) { this.certificate = certificate; }
}
