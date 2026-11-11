package com.herpathway.model;

import java.time.LocalDateTime;

/** One message in a mentee/mentor chat (table {@code ChatSession}). */
public class ChatMessage {

    private final long senderId;
    private final String message;
    private final LocalDateTime sentAt;

    public ChatMessage(long senderId, String message, LocalDateTime sentAt) {
        this.senderId = senderId;
        this.message = message;
        this.sentAt = sentAt;
    }

    public long getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public LocalDateTime getSentAt() { return sentAt; }
}
