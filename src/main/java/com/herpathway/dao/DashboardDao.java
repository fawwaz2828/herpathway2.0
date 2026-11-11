package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Numbers shown on the home dashboard. */
public class DashboardDao {

    public int countMentors() throws SQLException {
        return count("SELECT COUNT(*) FROM form_Mentor");
    }

    public int countMentees() throws SQLException {
        return count("SELECT COUNT(*) FROM form_Mentee");
    }

    public int countThreads() throws SQLException {
        return count("SELECT COUNT(*) FROM forum_thread");
    }

    public int countLearningItems() throws SQLException {
        return count("SELECT COUNT(*) FROM learning_content");
    }

    public int countLearningItemsBy(long authorId) throws SQLException {
        return count("SELECT COUNT(*) FROM learning_content WHERE authorId = ?", authorId);
    }

    public int countThreadsBy(long authorId) throws SQLException {
        return count("SELECT COUNT(*) FROM forum_thread WHERE authorId = ?", authorId);
    }

    /** Mentees currently assigned to this mentor. */
    public int countMenteesOf(long mentorId) throws SQLException {
        return count("SELECT COUNT(*) FROM mentor_assignment WHERE mentorId = ?", mentorId);
    }

    /** Applications to this mentor that still need a decision. */
    public int countPendingRequests(long mentorId) throws SQLException {
        return count("SELECT COUNT(*) FROM kuisioner k WHERE k.userIdMentor = ? AND k.status = 'pending' "
                + "AND NOT EXISTS (SELECT 1 FROM mentor_assignment a WHERE a.menteeId = k.userIdMentee)", mentorId);
    }

    private static int count(String sql, long... params) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setLong(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
