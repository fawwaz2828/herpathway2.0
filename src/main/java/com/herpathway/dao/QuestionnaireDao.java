package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.Questionnaire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Mentee applications to mentors (table {@code kuisioner}). */
public class QuestionnaireDao {

    private static final String SELECT =
            "SELECT k.userIdMentee, k.userIdMentor, k.experience_level_mentee, k.beneficial_aspects, k.suggestions, "
            + "k.status, k.createdAt, m.displayName AS menteeName, m.profilePicturePath, "
            + "mo.displayName AS mentorName, mo.currentJob, mo.companyName, a.mentorId AS assignedMentorId "
            + "FROM kuisioner k "
            + "LEFT JOIN form_Mentee m ON m.userId = k.userIdMentee "
            + "LEFT JOIN form_Mentor mo ON mo.userId = k.userIdMentor "
            + "LEFT JOIN mentor_assignment a ON a.menteeId = k.userIdMentee ";

    public void insert(Questionnaire q) throws SQLException {
        String sql = "INSERT INTO kuisioner (userIdMentee, userIdMentor, experience_level_mentee, beneficial_aspects, "
                + "suggestions, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, q.getMenteeId());
            ps.setLong(2, q.getMentorId());
            ps.setString(3, q.getExperienceLevel());
            ps.setString(4, q.getBeneficialAspects());
            ps.setString(5, q.getSuggestions());
            ps.setString(6, Questionnaire.PENDING);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Data entry failed, no rows were affected.");
            }
        }
    }

    /** Whether {@code menteeId} has already applied to {@code mentorId}. */
    public boolean exists(long menteeId, long mentorId) throws SQLException {
        return find(menteeId, mentorId) != null;
    }

    /** @return the mentee's (latest) application to that mentor, or {@code null} */
    public Questionnaire find(long menteeId, long mentorId) throws SQLException {
        List<Questionnaire> found = query(SELECT + "WHERE k.userIdMentee = ? AND k.userIdMentor = ? ORDER BY k.id DESC",
                menteeId, mentorId);
        return found.isEmpty() ? null : found.get(0);
    }

    /** Applications sent to {@code mentorId}, newest first, with the mentee's name and assignment. */
    public List<Questionnaire> findByMentor(long mentorId) throws SQLException {
        return query(SELECT + "WHERE k.userIdMentor = ? ORDER BY k.id DESC", mentorId);
    }

    /** Applications sent by {@code menteeId}, newest first, with the mentor's name and job. */
    public List<Questionnaire> findByMentee(long menteeId) throws SQLException {
        return query(SELECT + "WHERE k.userIdMentee = ? ORDER BY k.id DESC", menteeId);
    }

    /** Marks the mentee's pending application as declined. */
    public void decline(long menteeId, long mentorId) throws SQLException {
        String sql = "UPDATE kuisioner SET status = ? WHERE userIdMentee = ? AND userIdMentor = ? AND status <> ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, Questionnaire.DECLINED);
            ps.setLong(2, menteeId);
            ps.setLong(3, mentorId);
            ps.setString(4, Questionnaire.ACCEPTED);
            ps.executeUpdate();
        }
    }

    private static List<Questionnaire> query(String sql, long... ids) throws SQLException {
        List<Questionnaire> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < ids.length; i++) {
                ps.setLong(i + 1, ids[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Questionnaire q = new Questionnaire(
                            rs.getLong("userIdMentee"),
                            rs.getLong("userIdMentor"),
                            rs.getString("experience_level_mentee"),
                            rs.getString("beneficial_aspects"),
                            rs.getString("suggestions"));
                    q.setStatus(rs.getString("status"));
                    q.setCreatedAt(ForumDao.toLocal(rs.getTimestamp("createdAt")));
                    q.setMenteeName(rs.getString("menteeName"));
                    q.setMenteePicturePath(rs.getString("profilePicturePath"));
                    q.setMentorName(rs.getString("mentorName"));
                    q.setMentorHeadline(headline(rs.getString("currentJob"), rs.getString("companyName")));
                    q.setAssignedMentorId(rs.getLong("assignedMentorId")); // 0 when NULL
                    result.add(q);
                }
            }
        }
        return result;
    }

    private static String headline(String job, String company) {
        boolean hasJob = job != null && !job.trim().isEmpty();
        boolean hasCompany = company != null && !company.trim().isEmpty();
        if (hasJob && hasCompany) {
            return job + " at " + company;
        }
        return hasJob ? job : hasCompany ? company : "";
    }
}
