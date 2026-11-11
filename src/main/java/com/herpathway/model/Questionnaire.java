package com.herpathway.model;

import java.time.LocalDateTime;

/** A mentee's application to a mentor (table {@code kuisioner}). */
public class Questionnaire {

    public static final String PENDING = "pending";
    public static final String ACCEPTED = "accepted";
    public static final String DECLINED = "declined";

    private final long menteeId;
    private final long mentorId;
    private final String experienceLevel;
    private final String beneficialAspects;
    private final String suggestions;

    // Filled in when read from the database.
    private String status = PENDING;
    private LocalDateTime createdAt;
    private long assignedMentorId;
    // Listing requests for a mentor:
    private String menteeName;
    private String menteePicturePath;
    // Listing a mentee's own applications:
    private String mentorName;
    private String mentorHeadline;

    public Questionnaire(long menteeId, long mentorId, String experienceLevel, String beneficialAspects, String suggestions) {
        this.menteeId = menteeId;
        this.mentorId = mentorId;
        this.experienceLevel = experienceLevel;
        this.beneficialAspects = beneficialAspects;
        this.suggestions = suggestions;
    }

    public long getMenteeId() { return menteeId; }
    public long getMentorId() { return mentorId; }
    public String getExperienceLevel() { return experienceLevel; }
    public String getBeneficialAspects() { return beneficialAspects; }
    public String getSuggestions() { return suggestions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status == null ? PENDING : status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getMenteeName() { return menteeName; }
    public void setMenteeName(String menteeName) { this.menteeName = menteeName; }

    public String getMenteePicturePath() { return menteePicturePath; }
    public void setMenteePicturePath(String menteePicturePath) { this.menteePicturePath = menteePicturePath; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    /** e.g. "Lead Data Scientist at Tokopedia". */
    public String getMentorHeadline() { return mentorHeadline; }
    public void setMentorHeadline(String mentorHeadline) { this.mentorHeadline = mentorHeadline; }

    /** Mentor the mentee is currently assigned to, or 0. */
    public long getAssignedMentorId() { return assignedMentorId; }
    public void setAssignedMentorId(long assignedMentorId) { this.assignedMentorId = assignedMentorId; }

    public boolean isAcceptedByThisMentor() { return assignedMentorId != 0 && assignedMentorId == mentorId; }
    public boolean isTakenByAnotherMentor() { return assignedMentorId != 0 && assignedMentorId != mentorId; }
    public boolean isDeclined() { return DECLINED.equals(status) && !isAcceptedByThisMentor(); }

    /** Still waiting for this mentor's decision. */
    public boolean isPending() { return !isDeclined() && assignedMentorId == 0; }
}
