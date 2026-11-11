package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.MenteeProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.herpathway.dao.ProfileMapper.COMMON_ASSIGNMENTS;
import static com.herpathway.dao.ProfileMapper.COMMON_COLUMNS;
import static com.herpathway.dao.ProfileMapper.COMMON_COUNT;
import static com.herpathway.dao.ProfileMapper.bindCommon;
import static com.herpathway.dao.ProfileMapper.placeholders;

/** Mentee profiles (table {@code form_Mentee}). */
public class MenteeDao {

    /** @return the profile, or {@code null} if the mentee hasn't filled in the form yet */
    public MenteeProfile findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM form_Mentee WHERE userId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean exists(long userId) throws SQLException {
        return findByUserId(userId) != null;
    }

    public void insert(MenteeProfile p) throws SQLException {
        String sql = "INSERT INTO form_Mentee (userId, " + COMMON_COLUMNS
                + ", mainGoal, fieldOfStudyGoal, expectations, profilePicturePath) VALUES ("
                + placeholders(1 + COMMON_COUNT + 4) + ")";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, p.getUserId());
            int i = bindCommon(ps, 2, p);
            bindExtra(ps, i, p);
            ps.executeUpdate();
        }
    }

    public void update(MenteeProfile p) throws SQLException {
        String sql = "UPDATE form_Mentee SET " + COMMON_ASSIGNMENTS
                + ", mainGoal = ?, fieldOfStudyGoal = ?, expectations = ?, profilePicturePath = ? WHERE userId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bindCommon(ps, 1, p);
            i = bindExtra(ps, i, p);
            ps.setLong(i, p.getUserId());
            ps.executeUpdate();
        }
    }

    /** Inserts the profile if it doesn't exist yet, otherwise updates it. */
    public void save(MenteeProfile p) throws SQLException {
        if (exists(p.getUserId())) {
            update(p);
        } else {
            insert(p);
        }
    }

    public void updateProfilePicturePath(long userId, String path) throws SQLException {
        String sql = "UPDATE form_Mentee SET profilePicturePath = ? WHERE userId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    private static int bindExtra(PreparedStatement ps, int i, MenteeProfile p) throws SQLException {
        ps.setString(i++, p.getMainGoal());
        ps.setString(i++, p.getFieldOfStudyGoal());
        ps.setString(i++, p.getExpectations());
        ps.setString(i++, p.getProfilePicturePath());
        return i;
    }

    private static MenteeProfile map(ResultSet rs) throws SQLException {
        MenteeProfile p = new MenteeProfile();
        ProfileMapper.readCommon(rs, p);
        p.setMainGoal(rs.getString("mainGoal"));
        p.setFieldOfStudyGoal(rs.getString("fieldOfStudyGoal"));
        p.setExpectations(rs.getString("expectations"));
        p.setProfilePicturePath(rs.getString("profilePicturePath"));
        return p;
    }
}
