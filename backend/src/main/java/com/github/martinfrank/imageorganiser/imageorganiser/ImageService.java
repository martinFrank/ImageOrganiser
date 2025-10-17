package com.github.martinfrank.imageorganiser.imageorganiser;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageService {

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

    public List<String> listFilesInImagedir() throws IOException {
        Path imagedir = Paths.get("/app/imagedir");
        return Files.list(imagedir)
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
    }
}