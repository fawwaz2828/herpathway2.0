package com.herpathway.dao;

import com.herpathway.database.DatabaseConnection;
import com.herpathway.model.User;
import com.herpathway.util.Passwords;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Accounts (table {@code User}). Passwords are stored hashed, see {@link Passwords}. */
public class UserDao {

    public static final String MENTEE = "mentee";
    public static final String MENTOR = "mentor";
    public static final String ORGANIZATION = "organization";

    /** @return the new user's id */
    public long insert(String username, String password, String email, String userType) throws SQLException {
        String sql = "INSERT INTO User (username, password, email, userType) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, Passwords.hash(password));
            ps.setString(3, email);
            ps.setString(4, userType);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }

    /** @return the matching user, or {@code null} if the username/password pair is wrong */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, email, userType FROM User WHERE username = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String stored = rs.getString("password");
                if (!Passwords.matches(password, stored)) {
                    return null;
                }
                User user = new User(rs.getString("username"), null, rs.getString("email"), rs.getString("userType"));
                user.setId(rs.getLong("id"));
                if (!Passwords.isHashed(stored)) {
                    upgradeToHash(c, user.getId(), password);
                }
                return user;
            }
        }
    }

    /** Accounts created before hashing was added are converted the first time they log in. */
    private void upgradeToHash(Connection c, long userId, String password) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE User SET password = ? WHERE id = ?")) {
            ps.setString(1, Passwords.hash(password));
            ps.setLong(2, userId);
            ps.executeUpdate();
        }
    }

    public boolean usernameExists(String username) throws SQLException {
        return exists("SELECT 1 FROM User WHERE username = ?", username);
    }

    public boolean emailExists(String email) throws SQLException {
        return exists("SELECT 1 FROM User WHERE email = ?", email);
    }

    private boolean exists(String sql, String value) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Whether the user has filled in the onboarding form for their role. */
    public boolean hasCompletedProfile(long userId, String userType) throws SQLException {
        String table;
        switch (userType) {
            case MENTEE:
                table = "form_Mentee";
                break;
            case MENTOR:
                table = "form_Mentor";
                break;
            case ORGANIZATION:
                table = "form_Organization";
                break;
            default:
                return false;
        }
        String sql = "SELECT 1 FROM " + table + " WHERE userId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
