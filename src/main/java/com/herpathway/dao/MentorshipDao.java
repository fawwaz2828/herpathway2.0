package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.ContactSummary;
import com.herpathway.model.MenteeProfile;
import com.herpathway.util.Images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Who mentors whom (table {@code mentor_assignment}).
 * A mentee has at most one mentor; accepting an application creates or replaces the assignment.
 */
public class MentorshipDao {

    /** Accepts {@code menteeId} as a mentee of {@code mentorId}. */
    public void accept(long menteeId, long mentorId) throws SQLException {
        String assign = "INSERT INTO mentor_assignment (menteeId, mentorId) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE mentorId = VALUES(mentorId)";
        String status = "UPDATE form_Mentee SET status = ? WHERE userId = ?";
        String application = "UPDATE kuisioner SET status = 'accepted' WHERE userIdMentee = ? AND userIdMentor = ?";
        try (Connection c = DatabaseConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement a = c.prepareStatement(assign);
                 PreparedStatement s = c.prepareStatement(status);
                 PreparedStatement k = c.prepareStatement(application)) {
                a.setLong(1, menteeId);
                a.setLong(2, mentorId);
                a.executeUpdate();
                s.setString(1, MenteeProfile.STATUS_ACCEPTED);
                s.setLong(2, menteeId);
                s.executeUpdate();
                k.setLong(1, menteeId);
                k.setLong(2, mentorId);
                k.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** @return the mentor assigned to {@code menteeId}, or {@code 0} if none */
    public long findMentorOf(long menteeId) throws SQLException {
        String sql = "SELECT mentorId FROM mentor_assignment WHERE menteeId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, menteeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong("mentorId") : 0L;
            }
        }
    }

    /** The mentee's mentor as a chat contact (empty list if none yet). */
    public List<ContactSummary> findMentorContactOf(long menteeId) throws SQLException {
        String sql = "SELECT m.userId, m.displayName, m.profilePicture FROM mentor_assignment a "
                + "JOIN form_Mentor m ON m.userId = a.mentorId WHERE a.menteeId = ?";
        List<ContactSummary> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, menteeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ContactSummary(rs.getLong("userId"), rs.getString("displayName"),
                            Images.fromBytes(rs.getBytes("profilePicture"))));
                }
            }
        }
        return result;
    }

    /** Mentees accepted by {@code mentorId}, as chat contacts. */
    public List<ContactSummary> findMenteeContactsOf(long mentorId) throws SQLException {
        String sql = "SELECT m.userId, m.displayName, m.profilePicturePath FROM mentor_assignment a "
                + "JOIN form_Mentee m ON m.userId = a.menteeId WHERE a.mentorId = ? ORDER BY m.displayName";
        List<ContactSummary> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, mentorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ContactSummary(rs.getLong("userId"), rs.getString("displayName"),
                            Images.fromPathOrNull(rs.getString("profilePicturePath"))));
                }
            }
        }
        return result;
    }
}
