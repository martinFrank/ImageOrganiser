package com.github.martinfrank.imageorganiser.imageorganiser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private final AtomicBoolean isUpdating = new AtomicBoolean(false);
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping
    public ResponseEntity<Page<Image>> getAllImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LOGGER.debug("REST call wurde ausgeführt: getAllImages with page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Image> images = imageService.findAll(pageable);
        return ResponseEntity.ok(images);
    }

    @PostMapping("/filtered")
    public ResponseEntity<Page<ImageDto>> getFilteredImages(
            @RequestBody List<ImageRequestDto> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LOGGER.debug("REST call wurde ausgeführt: getFilteredImages with filters={}, page={}, size={}", filters, page,
                size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ImageDto> images = imageService.findFilteredImages(filters, pageable);
        return ResponseEntity.ok(images);
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<Image> getImageById(@PathVariable Long id) {
    // return imageRepository.findById(id)
    // .map(image -> ResponseEntity.ok(image))
    // .orElse(ResponseEntity.notFound().build());
    // }

    @GetMapping("/island")
    public ResponseEntity<byte[]> getIslandImage() {
        LOGGER.debug("REST call wurde ausgeführt: getIslandImage");
        try {
            byte[] imageBytes = imageService.getIslandImage();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (IOException e) {
            LOGGER.error("Error loading island image: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/timestamp")
    public ResponseEntity<List<String>> updateImages(@RequestBody TimestampDto timestampDto) {
        LOGGER.debug("REST call wurde ausgeführt: timestamp {}", timestampDto.time());

        if (!isUpdating.compareAndSet(false, true)) {
            LOGGER.warn("Update already in progress, returning busy response");
            return ResponseEntity.status(409)
                    .body(Collections.singletonList("Server is busy processing another update request"));
        }

        try {
            List<String> scannedFiles = imageService.updateInventory();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(scannedFiles);
        } catch (IOException e) {
            LOGGER.error("Error updating image inventory: {}", e.getMessage());
            return ResponseEntity.status(500).body(Collections.emptyList());
        } finally {
            isUpdating.set(false);
        }
    }
}