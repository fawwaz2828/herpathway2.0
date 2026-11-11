package com.herpathway.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Reads uploaded images into bytes small enough to store in the database.
 * Large images are scaled down and re-encoded as JPEG so that a form with several
 * uploads still fits in one MySQL packet.
 */
public final class ImageCompressor {

    /** Images at or below this size are stored as-is. */
    public static final int MAX_IMAGE_SIZE = 300 * 1024; // 300 KB

    /** Longest side, in pixels, after scaling down. Plenty for a photo or a readable document scan. */
    private static final int MAX_DIMENSION = 1600;

    private static final float[] QUALITIES = {0.85f, 0.75f, 0.6f, 0.45f};

    private ImageCompressor() {
    }

    /**
     * @return the file's bytes, or a smaller JPEG version if larger than {@link #MAX_IMAGE_SIZE}
     * @throws IOException if the file can't be read or isn't an image
     */
    public static byte[] readWithinLimit(File file) throws IOException {
        byte[] data = Files.readAllBytes(file.toPath());
        if (data.length <= MAX_IMAGE_SIZE) {
            return data;
        }
        BufferedImage source = ImageIO.read(file);
        if (source == null) {
            throw new IOException("Unsupported image format: " + file.getName());
        }

        BufferedImage image = toRgb(source, MAX_DIMENSION);
        byte[] best = null;
        // Lower the quality, then the resolution, until it fits.
        for (int attempt = 0; attempt < 3; attempt++) {
            for (float quality : QUALITIES) {
                best = toJpeg(image, quality);
                if (best.length <= MAX_IMAGE_SIZE) {
                    return best;
                }
            }
            image = toRgb(image, Math.max(image.getWidth(), image.getHeight()) * 3 / 4);
        }
        return best; // still small (≈ under 1 MB) after scaling; good enough to store
    }

    /** Draws the image onto a white RGB canvas (JPEG has no alpha), scaled so its longest side ≤ maxSide. */
    private static BufferedImage toRgb(BufferedImage source, int maxSide) {
        double scale = Math.min(1.0, (double) maxSide / Math.max(source.getWidth(), source.getHeight()));
        int w = Math.max(1, (int) Math.round(source.getWidth() * scale));
        int h = Math.max(1, (int) Math.round(source.getHeight() * scale));
        BufferedImage rgb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(source, 0, 0, w, h, Color.WHITE, null);
        g.dispose();
        return rgb;
    }

    private static byte[] toJpeg(BufferedImage image, float quality) throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(out)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
        return out.toByteArray();
    }
}
