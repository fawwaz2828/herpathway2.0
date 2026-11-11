package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.MentorData;
import com.herpathway.model.MentorProfile;
import com.herpathway.util.Images;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.herpathway.dao.ProfileMapper.COMMON_ASSIGNMENTS;
import static com.herpathway.dao.ProfileMapper.COMMON_COLUMNS;
import static com.herpathway.dao.ProfileMapper.COMMON_COUNT;
import static com.herpathway.dao.ProfileMapper.bindCommon;
import static com.herpathway.dao.ProfileMapper.placeholders;

/** Mentor profiles (table {@code form_Mentor}). */
public class MentorDao {

    /** @return the profile including its profile picture, or {@code null} if not found */
    public MentorProfile findByUserId(long userId) throws SQLException {
        String sql = "SELECT * FROM form_Mentor WHERE userId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                MentorProfile p = new MentorProfile();
                ProfileMapper.readCommon(rs, p);
                p.setProfilePicture(rs.getBytes("profilePicture"));
                return p;
            }
        }
    }

    /** Summary cards for the mentee's "find a mentor" list. */
    public List<MentorData> findAllCards() throws SQLException {
        String sql = "SELECT userId, displayName, fieldOfStudy, lastEducationLevel, companyName, currentJob, profilePicture "
                + "FROM form_Mentor";
        List<MentorData> result = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new MentorData(
                        rs.getLong("userId"),
                        rs.getString("displayName"),
                        rs.getString("fieldOfStudy"),
                        rs.getString("lastEducationLevel"),
                        rs.getString("companyName"),
                        rs.getString("currentJob"),
                        Images.fromBytes(rs.getBytes("profilePicture"))));
            }
        }
        return result;
    }

    public void insert(MentorProfile p) throws SQLException {
        String sql = "INSERT INTO form_Mentor (userId, " + COMMON_COLUMNS
                + ", profilePicture, idCard, cv, certificate) VALUES ("
                + placeholders(1 + COMMON_COUNT + 4) + ")";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, p.getUserId());
            int i = bindCommon(ps, 2, p);
            ps.setBytes(i++, p.getProfilePicture());
            ps.setBytes(i++, p.getIdCard());
            ps.setBytes(i++, p.getCv());
            ps.setBytes(i, p.getCertificate());
            ps.executeUpdate();
        }
    }

    /** Updates the text fields only; uploaded documents are left untouched. */
    public void updateDetails(MentorProfile p) throws SQLException {
        String sql = "UPDATE form_Mentor SET " + COMMON_ASSIGNMENTS + " WHERE userId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = bindCommon(ps, 1, p);
            ps.setLong(i, p.getUserId());
            ps.executeUpdate();
        }
    }
}
