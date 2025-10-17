package com.github.martinfrank.imageorganiser.imageorganiser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<Page<Image>> getAllImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.debug("REST call wurde ausgeführt: getAllImages with page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Image> images = imageRepository.findAll(pageable);
        return ResponseEntity.ok(images);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
//        return imageRepository.findById(id)
//                .map(image -> ResponseEntity.ok(image))
//                .orElse(ResponseEntity.notFound().build());
//    }

    @GetMapping("/island")
    public ResponseEntity<byte[]> getIslandImage() {
        logger.debug("REST call wurde ausgeführt: getIslandImage");
        try {
            byte[] imageBytes = imageService.getIslandImage();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            logger.error("Error loading island image: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}