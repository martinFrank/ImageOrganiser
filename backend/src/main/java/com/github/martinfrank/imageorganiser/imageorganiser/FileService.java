package com.github.martinfrank.imageorganiser.imageorganiser;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService {

    public List<Path> listImageFilesInImagedir() throws IOException {
        Path imageDirectory = Paths.get("/app/imagedir");
        try (Stream<Path> paths = Files.walk(imageDirectory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(FileService::isImageFile)
                    .toList();
        }
    }

    private static boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif") ||
                fileName.endsWith(".bmp") || fileName.endsWith(".tiff") ||
                fileName.endsWith(".tif") || fileName.endsWith(".webp");
    }
}
