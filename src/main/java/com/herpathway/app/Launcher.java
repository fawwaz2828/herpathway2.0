package com.herpathway.app;

/**
 * Plain entry point that doesn't extend {@code Application}.
 * Lets the app start from a normal classpath (IDE run button, fat jar) without
 * the "JavaFX runtime components are missing" error.
 */
public final class Launcher {

    private Launcher() {
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}
