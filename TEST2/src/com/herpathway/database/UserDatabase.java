package com.herpathway.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabase {

    public static long addUser(String username, String password, String email, String userType) throws SQLException {
        String sql = "INSERT INTO User (username, password, email, userType) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, userType);
            statement.executeUpdate();

            // Dapatkan ID pengguna yang baru ditambahkan
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    public static long validateUser(String username, String password) throws SQLException {
        String sql = "SELECT id FROM User WHERE username = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id"); // Mengembalikan userId jika valid
                } else {
                    return -1; // Login gagal
                }
            }
        }
    }

    public static boolean userExists(String username) throws SQLException {
        String sql = "SELECT * FROM User WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public static String getUserType(String username) throws SQLException {
        String sql = "SELECT userType FROM User WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("userType");
                }
            }
        }
        return null; // Or throw an exception if user not found
    }

    public static long getUserId(String username) throws SQLException {
        String sql = "SELECT id FROM User WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("id");
                }
            }
        }
        throw new SQLException("User not found"); // Atau bisa mengembalikan nilai yang sesuai jika pengguna tidak ditemukan
    }
    public static boolean isUserDataComplete(long userId, String userType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ";
        switch (userType) {
            case "mentee":
                sql += "form_Mentee WHERE userId = ?";
                break;
            case "mentor":
                sql += "form_Mentor WHERE userId = ?";
                break;
            case "organization":
                sql += "form_Organization WHERE userId = ?";
                break;
            default:
                return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }

}


