package com.herpathway.util;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

/** Converts the two ways pictures are stored (BLOB bytes or file path) into JavaFX images. */
public final class Images {

    private static final String DEFAULT_PROFILE_RESOURCE = "/image/default_profile.png";

    private Images() {
    }

    /** @return the image, or {@code null} if there is no data. */
    public static Image fromBytes(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return new Image(new ByteArrayInputStream(data));
    }

    /**
     * Loads an uploaded picture by its stored path.
     * Paths saved before uploads moved into {@code uploads/} (e.g. {@code profile_pictures/x.jpg})
     * are still found.
     *
     * @return the image, or the default profile picture if the path is empty or the file is missing
     */
    public static Image fromPath(String path) {
        Image image = fromPathOrNull(path);
        return image != null ? image : defaultProfilePicture();
    }

    /** Like {@link #fromPath} but returns {@code null} when there is no picture (callers show initials instead). */
    public static Image fromPathOrNull(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            file = new File(FileStorage.UPLOAD_ROOT, path);
        }
        return file.exists() ? fromFile(file) : null;
    }

    public static Image fromFile(File file) {
        return file == null ? null : new Image(file.toURI().toString());
    }

    public static Image defaultProfilePicture() {
        URL url = Images.class.getResource(DEFAULT_PROFILE_RESOURCE);
        return url == null ? null : new Image(url.toExternalForm());
    }
}
