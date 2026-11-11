package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.ForumReply;
import com.herpathway.model.ForumThread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Forum threads, replies and likes (tables {@code forum_thread}, {@code forum_reply}, {@code forum_like}). */
public class ForumDao {

    private static final String THREAD_SELECT =
            "SELECT t.id, t.authorId, t.category, t.title, t.body, t.createdAt, u.userType, "
            + Authors.NAME + " AS authorName, "
            + "(SELECT COUNT(*) FROM forum_reply r WHERE r.threadId = t.id) AS replyCount, "
            + "(SELECT COUNT(*) FROM forum_like l WHERE l.threadId = t.id) AS likeCount, "
            + "(SELECT COUNT(*) FROM forum_like l WHERE l.threadId = t.id AND l.userId = ?) AS likedByViewer "
            + "FROM forum_thread t JOIN User u ON u.id = t.authorId" + Authors.JOINS;

    /**
     * @param category only this category, or {@code null} for all
     * @param query    text to look for in the title or body, or {@code null}
     * @param limit    maximum number of threads, newest first
     */
    public List<ForumThread> findThreads(long viewerId, String category, String query, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(THREAD_SELECT).append("WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        params.add(viewerId);
        if (category != null) {
            sql.append(" AND t.category = ?");
            params.add(category);
        }
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (LOWER(t.title) LIKE ? OR LOWER(t.body) LIKE ?)");
            String like = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
            params.add(like);
            params.add(like);
        }
        sql.append(" ORDER BY t.createdAt DESC, t.id DESC LIMIT ?");
        params.add(limit);

        List<ForumThread> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(readThread(rs));
                }
            }
        }
        return result;
    }

    /** @return the thread, or {@code null} if it was deleted */
    public ForumThread findThread(long threadId, long viewerId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(THREAD_SELECT + "WHERE t.id = ?")) {
            ps.setLong(1, viewerId);
            ps.setLong(2, threadId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? readThread(rs) : null;
            }
        }
    }

    /** Replies, oldest first. */
    public List<ForumReply> findReplies(long threadId) throws SQLException {
        String sql = "SELECT r.id, r.authorId, r.body, r.createdAt, u.userType, " + Authors.NAME + " AS authorName "
                + "FROM forum_reply r JOIN User u ON u.id = r.authorId" + Authors.JOINS
                + "WHERE r.threadId = ? ORDER BY r.createdAt, r.id";
        List<ForumReply> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, threadId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ForumReply(rs.getLong("id"), rs.getLong("authorId"), rs.getString("authorName"),
                            rs.getString("userType"), rs.getString("body"), toLocal(rs.getTimestamp("createdAt"))));
                }
            }
        }
        return result;
    }

    /** @return the new thread's id */
    public long createThread(long authorId, String category, String title, String body) throws SQLException {
        String sql = "INSERT INTO forum_thread (authorId, category, title, body) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, authorId);
            ps.setString(2, category);
            ps.setString(3, title);
            ps.setString(4, body);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Creating thread failed, no ID obtained.");
            }
        }
    }

    public void addReply(long threadId, long authorId, String body) throws SQLException {
        update("INSERT INTO forum_reply (threadId, authorId, body) VALUES (?, ?, ?)", threadId, authorId, body);
    }

    /** Deletes a thread (and its replies and likes); only its author can. */
    public boolean deleteThread(long threadId, long authorId) throws SQLException {
        return update("DELETE FROM forum_thread WHERE id = ? AND authorId = ?", threadId, authorId) > 0;
    }

    /** Deletes a reply; only its author can. */
    public boolean deleteReply(long replyId, long authorId) throws SQLException {
        return update("DELETE FROM forum_reply WHERE id = ? AND authorId = ?", replyId, authorId) > 0;
    }

    /** Likes the thread, or removes the like if the user already liked it. */
    public void toggleLike(long threadId, long userId) throws SQLException {
        if (update("DELETE FROM forum_like WHERE threadId = ? AND userId = ?", threadId, userId) == 0) {
            update("INSERT INTO forum_like (threadId, userId) VALUES (?, ?)", threadId, userId);
        }
    }

    private static int update(String sql, Object... params) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    private static ForumThread readThread(ResultSet rs) throws SQLException {
        return new ForumThread(rs.getLong("id"), rs.getLong("authorId"), rs.getString("authorName"),
                rs.getString("userType"), rs.getString("category"), rs.getString("title"), rs.getString("body"),
                toLocal(rs.getTimestamp("createdAt")), rs.getInt("replyCount"), rs.getInt("likeCount"),
                rs.getInt("likedByViewer") > 0);
    }

    static LocalDateTime toLocal(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }
}
