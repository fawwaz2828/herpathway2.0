package com.herpathway.navigation;

import com.herpathway.util.Alerts;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Central screen navigation.
 * <ul>
 *   <li>{@link #goTo(View)} replaces the whole window (login, forms, homepage shells).</li>
 *   <li>{@link #show(View)} swaps the content area of the current shell (Homepage / MentorHomepage).</li>
 *   <li>{@link #openWindow(View, String)} opens a separate window.</li>
 * </ul>
 * Controllers no longer need to reach other controllers through {@code stage.getUserData()}
 * or static {@code getInstance()} singletons.
 */
public final class Navigator {

    public static final double WIDTH = 1180;
    public static final double HEIGHT = 720;
    public static final double MIN_WIDTH = 1024;
    public static final double MIN_HEIGHT = 660;

    /** Global design system, applied to every window and dialog. */
    public static final String STYLESHEET = Navigator.class.getResource("/css/app.css").toExternalForm();

    private static Stage primaryStage;
    private static Pane contentArea;
    private static Consumer<View> onContentShown;
    private static Runnable shellRefresher;

    private Navigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Called by a shell controller so {@link #show(View)} knows where to put content.
     * {@code onShown} is told about every view shown there (used to highlight the sidebar).
     */
    public static void setContentArea(Pane area, Consumer<View> onShown) {
        contentArea = area;
        onContentShown = onShown;
    }

    /** Registered by the shell so pages can ask it to re-read the user's name and picture. */
    public static void setShellRefresher(Runnable refresher) {
        shellRefresher = refresher;
    }

    /** Re-reads the name and picture shown in the sidebar (after the profile was edited). */
    public static void refreshShell() {
        if (shellRefresher != null) {
            shellRefresher.run();
        }
    }

    /** Replaces the whole window with {@code view}. */
    public static <T> T goTo(View view) {
        return goTo(view, null);
    }

    /** Replaces the whole window; {@code setup} runs on the new controller before it's shown. */
    public static <T> T goTo(View view, Consumer<T> setup) {
        // A new full-window scene drops the old shell, so forget its content area.
        // This must happen before loading: a shell registers its own area in initialize().
        Pane previousArea = contentArea;
        Consumer<View> previousListener = onContentShown;
        Runnable previousRefresher = shellRefresher;
        contentArea = null;
        onContentShown = null;
        shellRefresher = null;
        Loaded<T> loaded = load(view);
        if (loaded == null) {
            contentArea = previousArea;
            onContentShown = previousListener;
            shellRefresher = previousRefresher;
            return null;
        }
        if (setup != null) {
            setup.accept(loaded.controller);
        }
        Scene current = primaryStage.getScene();
        double w = current != null ? current.getWidth() : WIDTH;
        double h = current != null ? current.getHeight() : HEIGHT;
        primaryStage.setScene(styled(new Scene(loaded.root, w, h)));
        primaryStage.show();
        return loaded.controller;
    }

    /** Shows {@code view} inside the current shell's content area. */
    public static <T> T show(View view) {
        return show(view, null);
    }

    /** Shows {@code view} in the content area; {@code setup} runs on the new controller first. */
    public static <T> T show(View view, Consumer<T> setup) {
        if (contentArea == null) {
            Alerts.error("Navigation Error", "No content area is active to show " + view + ".");
            return null;
        }
        Loaded<T> loaded = load(view);
        if (loaded == null) {
            return null;
        }
        if (setup != null) {
            setup.accept(loaded.controller);
        }
        VBox.setVgrow(loaded.root, Priority.ALWAYS);
        if (loaded.root instanceof Region) {
            // The content area sits in a fit-to-height scroll pane. Without this, a page taller than
            // the window is squeezed to fit and its wrapped text is cut off instead of scrolling.
            ((Region) loaded.root).setMinHeight(Region.USE_PREF_SIZE);
        }
        contentArea.getChildren().setAll(loaded.root);
        if (onContentShown != null) {
            onContentShown.accept(view);
        }
        return loaded.controller;
    }

    /** Opens {@code view} in a new window. */
    public static <T> T openWindow(View view, String title, Consumer<T> setup) {
        Loaded<T> loaded = load(view);
        if (loaded == null) {
            return null;
        }
        if (setup != null) {
            setup.accept(loaded.controller);
        }
        Stage stage = new Stage();
        stage.setTitle(title);
        if (primaryStage != null) {
            stage.initOwner(primaryStage);
        }
        stage.setScene(styled(new Scene(loaded.root)));
        stage.show();
        return loaded.controller;
    }

    public static <T> T openWindow(View view, String title) {
        return openWindow(view, title, null);
    }

    private static Scene styled(Scene scene) {
        scene.getStylesheets().add(STYLESHEET);
        return scene;
    }

    /** For screens that are linked from the UI but don't have an FXML file yet. */
    public static void notAvailable(String feature) {
        Alerts.info("Coming Soon", feature + " is not available yet.");
    }

    private static <T> Loaded<T> load(View view) {
        URL url = Navigator.class.getResource(view.resourcePath());
        if (url == null) {
            Alerts.error("Load Error", "Screen not found: " + view.resourcePath());
            return null;
        }
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            return new Loaded<>(root, loader.getController());
        } catch (IOException e) {
            Alerts.error("Load Error", e);
            return null;
        }
    }

    private static final class Loaded<T> {
        final Parent root;
        final T controller;

        Loaded(Parent root, T controller) {
            this.root = root;
            this.controller = controller;
        }
    }
}
