package com.herpathway.session;

public class SessionManager {
    private static SessionManager instance;
    private long currentUserId;
    private String currentUserUsername;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUser(long userId, String username) {
        this.currentUserId = userId;
        this.currentUserUsername = username;
    }

    public long getCurrentUserId() {
        return currentUserId;
    }

    public String getCurrentUserUsername() {
        return currentUserUsername;
    }
}
