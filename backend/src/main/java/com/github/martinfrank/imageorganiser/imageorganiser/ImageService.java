package com.github.martinfrank.imageorganiser.imageorganiser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Page<Image> findAll(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    private record ImageSize(int widthInPx, int heightInPx) {
    }

    public byte[] getIslandImage() throws IOException {
        ClassPathResource resource = new ClassPathResource("island-1.jpg");
        try (InputStream inputStream = resource.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }

    public void updateImageInformation(Path imagePath) {
        File imageFile = imagePath.toFile();
        FileInformationEntity fileInformationEntity = new FileInformationEntity();
        fileInformationEntity.setFilename(imageFile.getAbsolutePath());
        fileInformationEntity.setHash(calculateFileHash(imageFile));
        ImageSize size = extractSize(imageFile);
        if (size != null) {
            fileInformationEntity.setWidth(size.widthInPx());
            fileInformationEntity.setHeight(size.heightInPx());
        }
        LOGGER.debug("new Image information {}", fileInformationEntity);

    }

    private ImageSize extractSize(File imageFile) {
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

    private String calculateFileHash(File file) {
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