package com.herpathway.controller;

import com.herpathway.dao.MentorshipDao;
import com.herpathway.model.ContactSummary;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import javafx.fxml.FXML;

import java.sql.SQLException;
import java.util.List;

/** Chat screen seen by a mentee: only their assigned mentor. */
public class MenteeChatSessionController extends AbstractChatSessionController {

    private final MentorshipDao mentorshipDao = new MentorshipDao();

    @Override
    protected List<ContactSummary> loadPartners() throws SQLException {
        return mentorshipDao.findMentorContactOf(selfId());
    }

    @Override
    protected void showNoPartnersState() {
        setEmptyState("No mentor yet",
                "Apply to a mentor first. Once they accept your application, you can chat with them here.");
    }

    @FXML
    private void handleEmptyAction() {
        Navigator.show(View.MENTOR_MENU);
    }

    @Override protected void setSelfId(long selfId) { menteeId = selfId; }
    @Override protected void setPartnerId(long partnerId) { mentorId = partnerId; }
    @Override protected long partnerId() { return mentorId; }
}
