package com.herpathway.model;

import java.time.LocalDateTime;

/** A reply in a forum thread (table {@code forum_reply}). */
public class ForumReply {

    private final long id;
    private final long authorId;
    private final String authorName;
    private final String authorRole;
    private final String body;
    private final LocalDateTime createdAt;

    public ForumReply(long id, long authorId, String authorName, String authorRole, String body, LocalDateTime createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.body = body;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public long getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getAuthorRole() { return authorRole; }
    public String getBody() { return body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
