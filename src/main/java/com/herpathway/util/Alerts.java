package com.herpathway.util;

import javafx.application.Platform;
import com.herpathway.navigation.Navigator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Shared dialog helpers so controllers don't each re-implement showAlert(). */
public final class Alerts {

    private Alerts() {
    }

    public static void info(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }

    /** Yes/No question. @return true if the user confirmed */
    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(Navigator.STYLESHEET);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    public static void error(String title, String message) {
        show(Alert.AlertType.ERROR, title, message);
    }

    /** Logs the exception and shows an error dialog with its message. */
    public static void error(String title, Throwable t) {
        t.printStackTrace();
        error(title, t.getMessage());
    }

    private static void show(Alert.AlertType type, String title, String message) {
        // Alerts must be created on the JavaFX thread; hop over if called from a background thread.
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(type, title, message));
            return;
        }
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(Navigator.STYLESHEET);
        alert.showAndWait();
    }
}
