package com.herpathway.session;

import com.herpathway.model.MentorData;
import com.herpathway.model.User;

/** Holds who is logged in and which mentor the mentee is currently looking at. */
public final class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private long currentUserId;
    private String currentUserUsername;
    private String currentUserType;
    private MentorData currentMentorData;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void login(User user) {
        this.currentUserId = user.getId();
        this.currentUserUsername = user.getUsername();
        this.currentUserType = user.getUserType();
        this.currentMentorData = null;
    }

    public void clear() {
        currentUserId = 0;
        currentUserUsername = null;
        currentUserType = null;
        currentMentorData = null;
    }

    public long getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUserUsername() {
        return currentUserUsername;
    }

    public String getCurrentUserType() {
        return currentUserType;
    }

    public MentorData getCurrentMentorData() {
        return currentMentorData;
    }

    public void setCurrentMentorData(MentorData currentMentorData) {
        this.currentMentorData = currentMentorData;
    }

    /** @return the id of the mentor being viewed/applied to, or {@code 0} if none */
    public long getCurrentMentorId() {
        return currentMentorData != null ? currentMentorData.getUserId() : 0;
    }
}
