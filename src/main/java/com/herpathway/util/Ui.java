package com.herpathway.util;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Small UI helpers shared by controllers. */
public final class Ui {

    /** Outline icons, drawn on a 24x24 grid (see {@link #icon}). */
    public static final String ICON_HOME = "M3 10.5 12 3l9 7.5V20a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1z";
    public static final String ICON_SEARCH = "M11 4a7 7 0 1 0 0 14 7 7 0 0 0 0-14z M20.5 20.5 16 16";
    public static final String ICON_USERS = "M16 20v-1.5a3.5 3.5 0 0 0-3.5-3.5h-5A3.5 3.5 0 0 0 4 18.5V20 "
            + "M10 11a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z M20 20v-1.5a3.5 3.5 0 0 0-2.5-3.35 M15.5 4.2a3.5 3.5 0 0 1 0 6.6";
    public static final String ICON_MESSAGE = "M21 6v10a1 1 0 0 1-1 1H8l-5 4V6a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1z";
    public static final String ICON_FORUM = "M3 4h12v8H7l-4 3z M9 15.5h8l4 3V8.5h-3";
    public static final String ICON_BOOK = "M4 5a2 2 0 0 1 2-2h13v15H6a2 2 0 0 0-2 2z M4 20a2 2 0 0 0 2 2h13v-4";
    public static final String ICON_USER = "M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8z M5 21v-1a6 6 0 0 1 6-6h2a6 6 0 0 1 6 6v1";
    public static final String ICON_BUILDING = "M4 21V5a1 1 0 0 1 1-1h9a1 1 0 0 1 1 1v16 M15 10h4a1 1 0 0 1 1 1v10 "
            + "M2 21h20 M8 8h3 M8 12h3 M8 16h3";
    public static final String ICON_LOGOUT = "M9 4H5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h4 M15 8l4 4-4 4 M19 12H9";

    private static final String[] AVATAR_COLORS = {
            "#0F766E", "#4F46E5", "#B45309", "#BE185D", "#1D4ED8", "#6D28D9", "#047857", "#B91C1C"};
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    private Ui() {
    }

    /** Shows {@code image} (or the default picture) in {@code view}, cropped to a circle. */
    public static void avatar(ImageView view, Image image) {
        if (view == null) {
            return;
        }
        view.setImage(image != null ? image : Images.defaultProfilePicture());
        view.setPreserveRatio(false);
        double r = Math.min(view.getFitWidth(), view.getFitHeight()) / 2;
        view.setClip(new Circle(r, r, r));
    }

    /** Circular avatar: the picture if there is one, otherwise the person's initials on a color. */
    public static Node avatar(Image image, String name, double size) {
        if (image != null && !image.isError()) {
            ImageView view = new ImageView();
            view.setFitWidth(size);
            view.setFitHeight(size);
            avatar(view, image);
            return view;
        }
        String key = name == null ? "" : name.trim();
        Circle circle = new Circle(size / 2);
        circle.setStyle("-fx-fill: " + AVATAR_COLORS[Math.floorMod(key.hashCode(), AVATAR_COLORS.length)] + ";");
        Label initials = new Label(initials(key));
        initials.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: " + Math.round(size * 0.38) + "px;");
        StackPane pane = new StackPane(circle, initials);
        pane.setMinSize(size, size);
        pane.setMaxSize(size, size);
        return pane;
    }

    /** Up to two initials, e.g. "Dr. Rina Wijaya" gives "RW". */
    static String initials(String name) {
        StringBuilder sb = new StringBuilder();
        for (String word : name.split("\\s+")) {
            if (word.isEmpty() || word.endsWith(".") || !Character.isLetterOrDigit(word.charAt(0))) {
                continue; // skip titles like "Dr." and stray symbols
            }
            sb.append(Character.toUpperCase(word.charAt(0)));
        }
        if (sb.length() == 0) {
            return name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase(Locale.ROOT);
        }
        return sb.length() <= 2 ? sb.toString() : "" + sb.charAt(0) + sb.charAt(sb.length() - 1);
    }

    /** An outline icon in a fixed 20x20 box, colored through the {@code icon} style class. */
    public static Node icon(String svgPath) {
        SVGPath path = new SVGPath();
        path.setContent(svgPath);
        path.getStyleClass().add("icon");
        path.setScaleX(0.82);
        path.setScaleY(0.82);
        StackPane box = new StackPane(path);
        box.setMinSize(20, 20);
        box.setPrefSize(20, 20);
        box.setMaxSize(20, 20);
        return box;
    }

    /** Highlights {@code active} and un-highlights the other sidebar buttons. */
    public static void setActive(Button active, Button... all) {
        for (Button b : all) {
            if (b != null) {
                b.getStyleClass().remove("active");
            }
        }
        if (active != null) {
            active.getStyleClass().add("active");
        }
    }

    /** Centered "nothing here yet" block. */
    public static VBox emptyState(String title, String message) {
        Label t = new Label(title);
        t.getStyleClass().add("empty-title");
        Label m = new Label(message);
        m.getStyleClass().add("muted");
        m.setWrapText(true);
        m.setMaxWidth(420);
        m.setAlignment(Pos.CENTER);
        VBox box = new VBox(t, m);
        box.getStyleClass().add("empty-state");
        return box;
    }

    public static Label badge(String text, String variant) {
        Label l = new Label(text);
        l.getStyleClass().add("badge");
        if (variant != null) {
            l.getStyleClass().add(variant);
        }
        return l;
    }

    public static Label label(String text, String... styleClasses) {
        Label l = new Label(text);
        l.getStyleClass().addAll(styleClasses);
        return l;
    }

    /** Shows or hides a node and removes it from layout when hidden. */
    public static void setShown(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    /** "just now", "5 min ago", "3 h ago", "2 days ago", then a date. */
    public static String timeAgo(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        long minutes = Duration.between(time, LocalDateTime.now()).toMinutes();
        if (minutes < 1) {
            return "just now";
        }
        if (minutes < 60) {
            return minutes + " min ago";
        }
        if (minutes < 60 * 24) {
            return (minutes / 60) + " h ago";
        }
        long days = minutes / (60 * 24);
        if (days < 7) {
            return days == 1 ? "yesterday" : days + " days ago";
        }
        return DATE.format(time);
    }

    /** "Mentee", "Mentor" or "Organization". */
    public static String roleLabel(String userType) {
        if (userType == null || userType.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(userType.charAt(0)) + userType.substring(1);
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String orDash(String s) {
        return isBlank(s) ? "-" : s.trim();
    }
}
