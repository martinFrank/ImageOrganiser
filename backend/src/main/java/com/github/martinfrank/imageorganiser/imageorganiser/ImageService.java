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
import java.util.Optional;

@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;
    private final ImageInformationRepository imageInformationRepository;

    public ImageService(ImageRepository imageRepository, ImageInformationRepository imageInformationRepository) {
        this.imageRepository = imageRepository;
        this.imageInformationRepository = imageInformationRepository;
    }

    public Page<Image> findAll(Pageable pageable) {
        return imageRepository.findAll(pageable);
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
        Optional<ImageInformationEntity> entity = createNonExistingImageInformation(imagePath);
        entity.ifPresent(imageInformationRepository::save);
        LOGGER.debug("new Image information {}", entity);
    }
    private Optional<ImageInformationEntity> createNonExistingImageInformation(Path imagePath) {
        File imageFile = imagePath.toFile();
        ImageInformationEntity fileInformationEntity = new ImageInformationEntity();
        String hash = FileUtil.calculateFileHash(imageFile);
        String filename = imageFile.getAbsolutePath();
        Optional<ImageInformationEntity> entity = imageInformationRepository.findByHashOrFileName(hash, filename);
        if(entity.isEmpty()) {
            fileInformationEntity.setFilename(filename);
            fileInformationEntity.setHash(hash);
            ImageSize size = FileUtil.extractSize(imageFile);
            if (size != null) {
                fileInformationEntity.setWidth(size.widthInPx());
                fileInformationEntity.setHeight(size.heightInPx());
                return Optional.of(fileInformationEntity);
            }
        }
        return Optional.empty();
    }


}