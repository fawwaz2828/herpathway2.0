package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.ContactSummary;
import com.herpathway.util.Images;
import javafx.scene.image.Image;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Looks up how a user appears to others (name and picture), whatever their role. */
public class UserDirectoryDao {

    /** @return the user's display name and picture ({@code null} picture if they have none), or {@code null} */
    public ContactSummary find(long userId) throws SQLException {
        String sql = "SELECT u.id, " + Authors.NAME + " AS name, am.profilePicturePath, ao.profilePicture "
                + "FROM User u" + Authors.JOINS + "WHERE u.id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Image picture = Images.fromBytes(rs.getBytes("profilePicture"));
                if (picture == null) {
                    picture = Images.fromPathOrNull(rs.getString("profilePicturePath"));
                }
                return new ContactSummary(rs.getLong("id"), rs.getString("name"), picture);
            }
        }
    }
}
