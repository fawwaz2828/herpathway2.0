package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.ChatMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Chat history (table {@code ChatSession}). */
public class ChatDao {

    public List<ChatMessage> findHistory(long menteeId, long mentorId) throws SQLException {
        String sql = "SELECT senderId, message, timestamp FROM ChatSession WHERE menteeId = ? AND mentorId = ? ORDER BY timestamp, id";
        List<ChatMessage> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, menteeId);
            ps.setLong(2, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ChatMessage(rs.getLong("senderId"), rs.getString("message"),
                            ForumDao.toLocal(rs.getTimestamp("timestamp"))));
                }
            }
        }
        return result;
    }

    public void save(long menteeId, long mentorId, long senderId, long receiverId, String message) throws SQLException {
        String sql = "INSERT INTO ChatSession (menteeId, mentorId, senderId, receiverId, message, timestamp) "
                + "VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, menteeId);
            ps.setLong(2, mentorId);
            ps.setLong(3, senderId);
            ps.setLong(4, receiverId);
            ps.setString(5, message);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Failed to save message.");
            }
        }
    }

}
