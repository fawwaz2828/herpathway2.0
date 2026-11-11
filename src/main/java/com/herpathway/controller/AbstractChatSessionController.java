package com.herpathway.controller;

import com.herpathway.chat.ChatClient;
import com.herpathway.dao.ChatDao;
import com.herpathway.model.ChatMessage;
import com.herpathway.model.ContactSummary;
import com.herpathway.session.SessionManager;
import com.herpathway.util.Alerts;
import com.herpathway.util.Ui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;

/**
 * Chat screen shared by mentees and mentors (MenteeChatSession.fxml / ChatSession.fxml use the same ids).
 * Subclasses only say who the possible partners are and which side of the conversation they are.
 */
public abstract class AbstractChatSessionController {

    @FXML
    protected ListView<ContactSummary> partnerList;
    @FXML
    protected VBox emptyPane;
    @FXML
    protected Label emptyTitle;
    @FXML
    protected Label emptyMessage;
    @FXML
    protected Button emptyActionButton;
    @FXML
    protected VBox conversationPane;
    @FXML
    protected StackPane partnerAvatar;
    @FXML
    protected Label partnerNameLabel;
    @FXML
    protected ScrollPane messagesScroll;
    @FXML
    protected VBox messagesBox;
    @FXML
    protected TextField inputField;

    protected long menteeId;
    protected long mentorId;

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DAY_TIME = DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale.ENGLISH);

    private final ChatDao chatDao = new ChatDao();
    private ChatClient chatClient;
    /** "No messages yet" row, removed once the first message appears. */
    private HBox emptyHint;

    // ---- provided by subclasses ----------------------------------------------------------

    /** People this user can chat with. */
    protected abstract List<ContactSummary> loadPartners() throws SQLException;

    /** Stores the partner's id in {@link #menteeId} or {@link #mentorId}. */
    protected abstract void setPartnerId(long partnerId);

    protected abstract long partnerId();

    /** Stores this user's own id in {@link #menteeId} or {@link #mentorId}. */
    protected abstract void setSelfId(long selfId);

    /** Text and button shown when there is nobody to chat with yet. */
    protected abstract void showNoPartnersState();

    // ---- lifecycle ---------------------------------------------------------------------

    @FXML
    public void initialize() {
        setSelfId(selfId());
        partnerList.setCellFactory(list -> new ContactCell());
        partnerList.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                openConversation(selected);
            }
        });

        List<ContactSummary> partners;
        try {
            partners = loadPartners();
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
            return;
        }
        partnerList.getItems().setAll(partners);
        if (partners.isEmpty()) {
            showNoPartnersState();
        } else {
            emptyTitle.setText("Pick a conversation");
            emptyActionButton.setVisible(false);
            emptyActionButton.setManaged(false);
            if (partners.size() == 1) {
                partnerList.getSelectionModel().selectFirst();
            }
        }

        chatClient = ChatClient.connect(this::onLiveMessage);
        // Close the socket when this screen is swapped out, instead of leaking one per visit.
        messagesBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null && chatClient != null) {
                chatClient.close();
                chatClient = null;
            }
        });
    }

    protected long selfId() {
        return SessionManager.getInstance().getCurrentUserId();
    }

    /** Opens the conversation with a specific partner, if they are in the list. */
    public void openConversationWith(long partnerId) {
        for (ContactSummary c : partnerList.getItems()) {
            if (c.getUserId() == partnerId) {
                partnerList.getSelectionModel().select(c);
                return;
            }
        }
    }

    private void openConversation(ContactSummary partner) {
        setPartnerId(partner.getUserId());
        partnerNameLabel.setText(partner.getDisplayName());
        partnerAvatar.getChildren().setAll(Ui.avatar(partner.getPicture(), partner.getDisplayName(), 40));
        emptyPane.setVisible(false);
        conversationPane.setVisible(true);
        loadChatHistory();
        inputField.requestFocus();
    }

    private void loadChatHistory() {
        messagesBox.getChildren().clear();
        try {
            for (ChatMessage m : chatDao.findHistory(menteeId, mentorId)) {
                addBubble(m.getMessage(), m.getSenderId() == selfId(), m.getSentAt());
            }
        } catch (SQLException e) {
            Alerts.error("Database Error", e);
        }
        if (messagesBox.getChildren().isEmpty()) {
            Label hint = new Label("No messages yet. Say hello!");
            hint.getStyleClass().add("muted");
            emptyHint = new HBox(hint);
            emptyHint.setAlignment(Pos.CENTER);
            messagesBox.getChildren().add(emptyHint);
        }
        scrollToBottom();
    }

    // ---- sending & receiving -----------------------------------------------------------

    @FXML
    protected void handleSendButtonAction() {
        String message = inputField.getText();
        if (message == null || message.trim().isEmpty() || menteeId == 0 || mentorId == 0) {
            return;
        }
        message = message.trim();
        try {
            chatDao.save(menteeId, mentorId, selfId(), partnerId(), message);
        } catch (SQLException e) {
            Alerts.error("Couldn't send message", e);
            return;
        }
        addBubble(message, true, LocalDateTime.now());
        scrollToBottom();
        inputField.clear();
        if (chatClient != null) {
            chatClient.send(menteeId + "|" + mentorId + "|" + selfId() + "|" + message);
        }
    }

    /** Line from the chat server: {@code menteeId|mentorId|senderId|text}. */
    private void onLiveMessage(String line) {
        String[] parts = line.split("\\|", 4);
        if (parts.length < 4) {
            return;
        }
        try {
            long lineMentee = Long.parseLong(parts[0]);
            long lineMentor = Long.parseLong(parts[1]);
            long sender = Long.parseLong(parts[2]);
            // Only messages for the open conversation, and not our own (already shown when sent).
            if (lineMentee == menteeId && lineMentor == mentorId && sender != selfId()) {
                addBubble(parts[3], false, LocalDateTime.now());
                scrollToBottom();
            }
        } catch (NumberFormatException ignored) {
            // Not our protocol; ignore.
        }
    }

    private void addBubble(String text, boolean mine, LocalDateTime sentAt) {
        if (emptyHint != null) {
            messagesBox.getChildren().remove(emptyHint);
            emptyHint = null;
        }
        Label bubble = new Label(text);
        bubble.setWrapText(true);
        bubble.setMaxWidth(420);
        bubble.getStyleClass().addAll("bubble", mine ? "bubble-mine" : "bubble-theirs");
        Label time = new Label(formatTime(sentAt));
        time.getStyleClass().add("bubble-time");
        VBox stack = new VBox(3, bubble, time);
        stack.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        HBox row = new HBox(stack);
        row.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messagesBox.getChildren().add(row);
    }

    /** "14:05" for today, "3 Mar, 14:05" for older messages. */
    private static String formatTime(LocalDateTime sentAt) {
        if (sentAt == null) {
            return "";
        }
        boolean today = sentAt.toLocalDate().equals(LocalDate.now());
        return (today ? TIME : DAY_TIME).format(sentAt);
    }

    private void scrollToBottom() {
        Platform.runLater(() -> messagesScroll.setVvalue(1.0));
    }

    // ---- helpers for subclasses ----------------------------------------------------------

    protected void setEmptyState(String title, String message) {
        emptyTitle.setText(title);
        emptyMessage.setText(message);
    }

    /** List row: round picture + name. */
    private static final class ContactCell extends ListCell<ContactSummary> {
        @Override
        protected void updateItem(ContactSummary item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            }
            Label name = new Label(item.getDisplayName());
            name.getStyleClass().add("strong");
            HBox row = new HBox(10, Ui.avatar(item.getPicture(), item.getDisplayName(), 36), name);
            row.setAlignment(Pos.CENTER_LEFT);
            setGraphic(row);
            setText(null);
        }
    }
}
