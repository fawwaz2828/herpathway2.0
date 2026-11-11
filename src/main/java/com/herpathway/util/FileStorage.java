package com.herpathway.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Picking files from disk and copying uploads into the app's storage folders. */
public final class FileStorage {

    /** Root folder for user uploads, relative to the working directory. Git-ignored. */
    public static final String UPLOAD_ROOT = "uploads";

    public static final String PROFILE_PICTURES = UPLOAD_ROOT + "/profile_pictures";
    public static final String FOUNDING_DOCUMENTS = UPLOAD_ROOT + "/founding_documents";
    public static final String FINANCIAL_REPORTS = UPLOAD_ROOT + "/financial_reports";

    public static final FileChooser.ExtensionFilter IMAGES =
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg");
    public static final FileChooser.ExtensionFilter PDF =
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf");

    private FileStorage() {
    }

    /** Opens a file chooser; returns {@code null} if the user cancels. */
    public static File choose(Window owner, String title, FileChooser.ExtensionFilter... filters) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        chooser.getExtensionFilters().addAll(filters);
        return chooser.showOpenDialog(owner);
    }

    /**
     * Copies {@code file} into {@code directory}, prefixing the name with a timestamp so
     * uploads with the same name don't overwrite (or collide with) each other.
     *
     * @return the stored path, relative to the working directory
     */
    public static String store(File file, String directory) throws IOException {
        Path dir = Paths.get(directory);
        Files.createDirectories(dir);
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Path destination = dir.resolve(timestamp + "_" + file.getName());
        Files.copy(file.toPath(), destination);
        return destination.toString();
    }
}
