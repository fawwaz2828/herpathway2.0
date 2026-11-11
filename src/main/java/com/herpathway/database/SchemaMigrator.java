package com.herpathway.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Locale;

/**
 * Creates missing tables and columns when the app starts, so a new feature never needs a manual SQL step.
 * Table definitions live in {@code resources/db/schema.sql}; every statement there is idempotent.
 */
final class SchemaMigrator {

    private static final String SCHEMA_RESOURCE = "/db/schema.sql";

    private SchemaMigrator() {
    }

    static void migrate(Connection c) throws SQLException {
        for (String statement : schemaStatements()) {
            try (Statement st = c.createStatement()) {
                st.execute(statement);
            }
        }
        // Columns added after the first release: CREATE TABLE IF NOT EXISTS won't touch existing tables.
        addColumnIfMissing(c, "kuisioner", "status", "VARCHAR(20) NOT NULL DEFAULT 'pending'");
        addColumnIfMissing(c, "kuisioner", "createdAt", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
        StarterContent.insertIfEmpty(c);
    }

    private static String[] schemaStatements() throws SQLException {
        String sql;
        try (InputStream in = SchemaMigrator.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in == null) {
                throw new SQLException("Missing resource " + SCHEMA_RESOURCE);
            }
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new SQLException("Couldn't read " + SCHEMA_RESOURCE, e);
        }
        sql = sql.replaceAll("(?m)^\\s*--.*$", "");
        return Arrays.stream(sql.split(";"))
                .map(String::trim)
                // The connection URL already selects the database.
                .filter(s -> !s.isEmpty() && !upper(s).startsWith("CREATE DATABASE") && !upper(s).startsWith("USE "))
                .toArray(String[]::new);
    }

    private static String upper(String s) {
        return s.toUpperCase(Locale.ROOT);
    }

    private static void addColumnIfMissing(Connection c, String table, String column, String definition) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet ignored = st.executeQuery("SELECT " + column + " FROM " + table + " WHERE 1 = 0")) {
            return; // column exists
        } catch (SQLException missing) {
            try (Statement st = c.createStatement()) {
                st.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            }
        }
    }
}
