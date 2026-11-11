package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.LearningItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** The learning library (table {@code learning_content}). */
public class LearningDao {

    /** Shown as the author of the starter content that ships with the app. */
    public static final String TEAM_NAME = "HerPathway team";

    private static final String SELECT =
            "SELECT lc.id, lc.authorId, lc.type, lc.title, lc.summary, lc.body, lc.url, lc.createdAt, "
            + "CASE WHEN lc.authorId IS NULL THEN '" + TEAM_NAME + "' ELSE " + Authors.NAME + " END AS authorName "
            + "FROM learning_content lc LEFT JOIN User u ON u.id = lc.authorId" + Authors.JOINS;

    /**
     * @param type  {@link LearningItem#ARTICLE}, {@code VIDEO}, {@code BOOK}, or {@code null} for all
     * @param query text to look for in the title or summary, or {@code null}
     */
    public List<LearningItem> find(String type, String query, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (type != null) {
            sql.append(" AND lc.type = ?");
            params.add(type);
        }
        if (query != null && !query.trim().isEmpty()) {
            sql.append(" AND (LOWER(lc.title) LIKE ? OR LOWER(lc.summary) LIKE ?)");
            String like = "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
            params.add(like);
            params.add(like);
        }
        sql.append(" ORDER BY lc.createdAt DESC, lc.id DESC LIMIT ?");
        params.add(limit);

        List<LearningItem> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(read(rs));
                }
            }
        }
        return result;
    }

    /** @return the item, or {@code null} if it was deleted */
    public LearningItem findById(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE lc.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? read(rs) : null;
            }
        }
    }

    /** @return the new item's id */
    public long insert(long authorId, String type, String title, String summary, String body, String url) throws SQLException {
        String sql = "INSERT INTO learning_content (authorId, type, title, summary, body, url) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, authorId);
            ps.setString(2, type);
            ps.setString(3, title);
            ps.setString(4, summary);
            setNullable(ps, 5, body);
            setNullable(ps, 6, url);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Publishing failed, no ID obtained.");
            }
        }
    }

    /** Deletes an item; only its author can. */
    public boolean delete(long id, long authorId) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM learning_content WHERE id = ? AND authorId = ?")) {
            ps.setLong(1, id);
            ps.setLong(2, authorId);
            return ps.executeUpdate() > 0;
        }
    }

    private static void setNullable(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }

    private static LearningItem read(ResultSet rs) throws SQLException {
        return new LearningItem(rs.getLong("id"), rs.getLong("authorId"), rs.getString("authorName"),
                rs.getString("type"), rs.getString("title"), rs.getString("summary"), rs.getString("body"),
                rs.getString("url"), ForumDao.toLocal(rs.getTimestamp("createdAt")));
    }
}
