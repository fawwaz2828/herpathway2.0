package com.herpathway.model;

import java.time.LocalDateTime;

/** An article, video or e-book in the learning library (table {@code learning_content}). */
public class LearningItem {

    public static final String ARTICLE = "article";
    public static final String VIDEO = "video";
    public static final String BOOK = "book";

    private final long id;
    /** 0 for the starter content that ships with the app. */
    private final long authorId;
    private final String authorName;
    private final String type;
    private final String title;
    private final String summary;
    private final String body;
    private final String url;
    private final LocalDateTime createdAt;

    public LearningItem(long id, long authorId, String authorName, String type, String title, String summary,
                        String body, String url, LocalDateTime createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.authorName = authorName;
        this.type = type;
        this.title = title;
        this.summary = summary;
        this.body = body;
        this.url = url;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public long getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getBody() { return body; }
    public String getUrl() { return url; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isArticle() { return ARTICLE.equals(type); }

    /** "Article", "Video" or "E-book". */
    public String typeLabel() {
        return labelOf(type);
    }

    public static String labelOf(String type) {
        if (VIDEO.equals(type)) {
            return "Video";
        }
        return BOOK.equals(type) ? "E-book" : "Article";
    }

    /** Rough reading time of an article, in minutes (200 words per minute). */
    public int readingMinutes() {
        if (body == null || body.trim().isEmpty()) {
            return 1;
        }
        return Math.max(1, (int) Math.round(body.trim().split("\\s+").length / 200.0));
    }
}
