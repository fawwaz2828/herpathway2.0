package com.herpathway.controller;

import com.herpathway.dao.UserDao;
import com.herpathway.dao.UserDirectoryDao;
import com.herpathway.model.ContactSummary;
import com.herpathway.navigation.Navigator;
import com.herpathway.navigation.View;
import com.herpathway.navigation.View.Section;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.EnumMap;
import java.util.Map;

/** Sidebar shell for every role (Shell.fxml). The menu depends on who is logged in. */
public class ShellController {

    @FXML
    private VBox contentArea;
    @FXML
    private VBox mainNav;
    @FXML
    private VBox accountNav;
    @FXML
    private StackPane avatarHolder;
    @FXML
    private Label profileNameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Button logoutButton;

    private final UserDirectoryDao directory = new UserDirectoryDao();
    private final Map<Section, Button> navButtons = new EnumMap<>(Section.class);

    @FXML
    public void initialize() {
        String role = SessionManager.getInstance().getCurrentUserType();
        roleLabel.setText(Ui.roleLabel(role));
        logoutButton.setGraphic(Ui.icon(Ui.ICON_LOGOUT));

        add(mainNav, Section.HOME, "Home", Ui.ICON_HOME, View.DASHBOARD);
        if (UserDao.MENTEE.equals(role)) {
            add(mainNav, Section.MENTORS, "Find a mentor", Ui.ICON_SEARCH, View.MENTOR_MENU);
            add(mainNav, Section.SESSIONS, "Messages", Ui.ICON_MESSAGE, View.MENTEE_CHAT);
        } else if (UserDao.MENTOR.equals(role)) {
            add(mainNav, Section.REQUESTS, "Mentee requests", Ui.ICON_USERS, View.MENTEE_MENU);
            add(mainNav, Section.SESSIONS, "Messages", Ui.ICON_MESSAGE, View.MENTOR_CHAT);
        }
        add(mainNav, Section.FORUM, "Forum", Ui.ICON_FORUM, View.FORUM);
        add(mainNav, Section.LEARNING, "Learning", Ui.ICON_BOOK, View.CONTENT);

        if (UserDao.ORGANIZATION.equals(role)) {
            add(accountNav, Section.PROFILE, "Organization profile", Ui.ICON_BUILDING, View.ORGANIZATION_PROFILE);
        } else {
            add(accountNav, Section.PROFILE, "My profile", Ui.ICON_USER,
                    UserDao.MENTOR.equals(role) ? View.MENTOR_PROFILE : View.MENTEE_PROFILE);
        }

        Navigator.setContentArea(contentArea, this::highlight);
        Navigator.setShellRefresher(this::refreshUserChip);
        refreshUserChip();
        Navigator.show(View.DASHBOARD);
    }

    private void add(VBox group, Section section, String text, String icon, View target) {
        Button button = new Button(text, Ui.icon(icon));
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> Navigator.show(target));
        navButtons.put(section, button);
        group.getChildren().add(button);
    }

    /** Re-reads the name and picture shown at the bottom of the sidebar. */
    private void refreshUserChip() {
        SessionManager session = SessionManager.getInstance();
        String name = session.getCurrentUserUsername();
        ContactSummary me = null;
        try {
            me = directory.find(session.getCurrentUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (me != null && !Ui.isBlank(me.getDisplayName())) {
            name = me.getDisplayName();
        }
        profileNameLabel.setText(name);
        avatarHolder.getChildren().setAll(Ui.avatar(me != null ? me.getPicture() : null, name, 36));
    }

    @FXML
    private void handleLogoutButtonAction() {
        if (Alerts.confirm("Log out", "Do you want to log out?")) {
            SessionManager.getInstance().clear();
            Navigator.goTo(View.LOGIN);
        }
    }

    private void highlight(View view) {
        Ui.setActive(navButtons.get(view.section()), navButtons.values().toArray(new Button[0]));
    }
}
