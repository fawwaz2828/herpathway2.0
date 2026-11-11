package com.herpathway.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A forum discussion (table {@code forum_thread}) with its author and counters. */
public class ForumThread {

    public static final List<String> CATEGORIES = Collections.unmodifiableList(Arrays.asList(
            "Career", "Technology", "Science", "Engineering", "Mathematics", "Study & scholarships", "General"));

    private final long id;
    private final long authorId;
    private final String authorName;
    private final String authorRole;
    private final String category;
    private final String title;
    private final String body;
    private final LocalDateTime createdAt;
    private final int replyCount;
    private final int likeCount;
    private final boolean likedByViewer;

    public ForumThread(long id, long authorId, String authorName, String authorRole, String category, String title,
                       String body, LocalDateTime createdAt, int replyCount, int likeCount, boolean likedByViewer) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorRole = authorRole;
        this.category = category;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.replyCount = replyCount;
        this.likeCount = likeCount;
        this.likedByViewer = likedByViewer;
    }

    public long getId() { return id; }
    public long getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getAuthorRole() { return authorRole; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getReplyCount() { return replyCount; }
    public int getLikeCount() { return likeCount; }
    public boolean isLikedByViewer() { return likedByViewer; }
}
