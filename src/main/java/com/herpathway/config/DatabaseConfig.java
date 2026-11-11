package com.herpathway.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Database settings, resolved in this order (first match wins):
 * <ol>
 *   <li>Environment variables {@code HERPATH_DB_URL}, {@code HERPATH_DB_USER}, {@code HERPATH_DB_PASSWORD}</li>
 *   <li>{@code db.properties} in the working directory</li>
 *   <li>{@code db.properties} on the classpath</li>
 *   <li>Built-in defaults (local MySQL, database {@code herpath}, user {@code root}, empty password)</li>
 * </ol>
 * Copy {@code db.properties.example} to {@code db.properties} and fill in your credentials.
 * {@code db.properties} is git-ignored so passwords never end up in the repository.
 */
public final class DatabaseConfig {

    private static final String FILE_NAME = "db.properties";

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/herpath";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final Properties FILE_PROPS = loadFileProperties();

    private DatabaseConfig() {
    }

    public static String url() {
        return resolve("HERPATH_DB_URL", "db.url", DEFAULT_URL);
    }

    public static String user() {
        return resolve("HERPATH_DB_USER", "db.user", DEFAULT_USER);
    }

    public static String password() {
        return resolve("HERPATH_DB_PASSWORD", "db.password", DEFAULT_PASSWORD);
    }

    private static String resolve(String envKey, String propKey, String fallback) {
        String env = System.getenv(envKey);
        if (env != null && !env.isEmpty()) {
            return env;
        }
        return FILE_PROPS.getProperty(propKey, fallback);
    }

    private static Properties loadFileProperties() {
        Properties props = new Properties();
        Path local = Paths.get(FILE_NAME);
        try {
            if (Files.exists(local)) {
                try (InputStream in = Files.newInputStream(local)) {
                    props.load(in);
                }
                return props;
            }
            try (InputStream in = DatabaseConfig.class.getResourceAsStream("/" + FILE_NAME)) {
                if (in != null) {
                    props.load(in);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
        return props;
    }
}
