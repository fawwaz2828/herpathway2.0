package com.herpathway.navigation;

/** Every screen in the app, mapped to its FXML file and the sidebar section it belongs to. */
public enum View {
    // Auth & onboarding (full-window scenes)
    LOGIN("LoginView.fxml", Section.NONE),
    REGISTER("RegisterView.fxml", Section.NONE),
    MENTEE_DATA_FORM("MenteeDataForm.fxml", Section.NONE),
    MENTOR_DATA_FORM("MentorDataForm.fxml", Section.NONE),
    ORGANIZATION_DATA_FORM("OrganizationDataForm.fxml", Section.NONE),

    // Shell: sidebar + content area, for every role
    HOME("Shell.fxml", Section.NONE),
    DASHBOARD("Dashboard.fxml", Section.HOME),

    // Mentee: finding and applying to a mentor
    MENTOR_MENU("MentorMenu.fxml", Section.MENTORS),
    MENTOR_PUBLIC_PROFILE("ProfilMentorView.fxml", Section.MENTORS),
    MENTORSHIP_QUESTION("MentorshipQuestion.fxml", Section.MENTORS),

    // Mentor: reviewing requests
    MENTEE_MENU("MenteeMenu.fxml", Section.REQUESTS),
    MENTEE_APPLICATION("MenteeApplication.fxml", Section.REQUESTS),

    // Chat
    MENTEE_CHAT("MenteeChatSession.fxml", Section.SESSIONS),
    MENTOR_CHAT("ChatSession.fxml", Section.SESSIONS),

    // Own profile
    MENTEE_PROFILE("UserProfileView.fxml", Section.PROFILE),
    MENTOR_PROFILE("MentorProfileView.fxml", Section.PROFILE),
    ORGANIZATION_PROFILE("OrganizationProfileView.fxml", Section.PROFILE),

    // Learning library
    CONTENT("Content.fxml", Section.LEARNING),
    CONTENT_DETAIL("ContentDetail.fxml", Section.LEARNING),
    CONTENT_EDITOR("ContentEditor.fxml", Section.LEARNING),

    // Forum
    FORUM("Forum.fxml", Section.FORUM),
    FORUM_THREAD("ForumThread.fxml", Section.FORUM),
    FORUM_COMPOSE("ForumCompose.fxml", Section.FORUM);

    /** Sidebar entry a screen is shown under. */
    public enum Section { NONE, HOME, MENTORS, REQUESTS, SESSIONS, FORUM, LEARNING, PROFILE }

    private static final String BASE = "/com/herpathway/view/";

    private final String fxml;
    private final Section section;

    View(String fxml, Section section) {
        this.fxml = fxml;
        this.section = section;
    }

    public String resourcePath() {
        return BASE + fxml;
    }

    public Section section() {
        return section;
    }
}
