package com.herpathway.database;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewUsers {

    public static void main(String[] args) {
        String sql = "select * from herpath.User;";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Username: " + resultSet.getString("username"));
                System.out.println("Email: " + resultSet.getString("email"));
                System.out.println("User Type: " + resultSet.getString("userType"));
                System.out.println("---------------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
