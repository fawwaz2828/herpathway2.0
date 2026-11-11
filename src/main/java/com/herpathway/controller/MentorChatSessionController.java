package com.herpathway.controller;

import com.herpathway.dao.MentorshipDao;
import com.herpathway.model.ContactSummary;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import javafx.fxml.FXML;

import java.sql.SQLException;
import java.util.List;

/** Chat screen seen by a mentor: the mentees they have accepted. */
public class MentorChatSessionController extends AbstractChatSessionController {

    private final MentorshipDao mentorshipDao = new MentorshipDao();

    /** Opens the conversation with a specific mentee (e.g. right after accepting them). */
    public void setMenteeId(long menteeId) {
        openConversationWith(menteeId);
    }

    @Override
    protected List<ContactSummary> loadPartners() throws SQLException {
        return mentorshipDao.findMenteeContactsOf(selfId());
    }

    @Override
    protected void showNoPartnersState() {
        setEmptyState("No mentees yet", "Accept a mentee request and your conversation will appear here.");
    }

    @FXML
    private void handleEmptyAction() {
        Navigator.show(View.MENTEE_MENU);
    }

    @Override protected void setSelfId(long selfId) { mentorId = selfId; }
    @Override protected void setPartnerId(long partnerId) { menteeId = partnerId; }
    @Override protected long partnerId() { return menteeId; }
}
