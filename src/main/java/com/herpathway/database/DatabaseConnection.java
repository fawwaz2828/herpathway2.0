package com.herpathway.database;

import com.herpathway.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The single place where JDBC connections are opened.
 * Only classes in {@code com.herpathway.dao} should call this.
 * The first successful connection also brings the schema up to date (see {@link SchemaMigrator}).
 */
public final class DatabaseConnection {

    private static boolean schemaReady;

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(DatabaseConfig.url(), DatabaseConfig.user(), DatabaseConfig.password());
        ensureSchema(c);
        return c;
    }

    private static synchronized void ensureSchema(Connection c) throws SQLException {
        if (schemaReady) {
            return;
        }
        try {
            SchemaMigrator.migrate(c);
            schemaReady = true;
        } catch (SQLException e) {
            c.close();
            throw new SQLException("Couldn't prepare the database tables: " + e.getMessage(), e);
        }
    }
}
