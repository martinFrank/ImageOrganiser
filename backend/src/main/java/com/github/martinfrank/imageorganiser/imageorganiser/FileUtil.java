package com.github.martinfrank.imageorganiser.imageorganiser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {

    private FileUtil(){
        throw new IllegalStateException("no instqance of utility class");
    }

    public static ImageSize extractSize(File imageFile) {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            if (image != null) {
                return new ImageSize(image.getWidth(), image.getHeight());
            }
        } catch (IOException e) {
            // Log error or handle appropriately
        }
        return null;
    }

    public static String calculateFileHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            // Log error or handle appropriately
            return null;
        }
    }
}
