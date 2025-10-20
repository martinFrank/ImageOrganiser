package com.github.martinfrank.imageorganiser.imageorganiser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    private final FileService fileService;

    private final ImageInformationRepository imageInformationRepository;

    private final ImagePredicateRepository imagePredicateRepository;

    public ImageService(ImageRepository imageRepository,
                        ImageInformationRepository imageInformationRepository,
                        FileService fileService,
                        ImagePredicateRepository imagePredicateRepository) {
        this.imageRepository = imageRepository;
        this.imageInformationRepository = imageInformationRepository;
        this.fileService = fileService;
        this.imagePredicateRepository = imagePredicateRepository;
    }

    public Page<Image> findAll(Pageable pageable) {
        return imageRepository.findAll(pageable);
    }

    public Page<ImageDto> findFilteredImages(List<ImageRequestDto> filters, Pageable pageable) {
        if (filters == null || filters.isEmpty()) {
            return findAll(pageable).map(this::convertToDto);
        }

        // Find image information IDs that match all filters
        List<Long> matchingImageIds = findMatchingImageIds(filters);

        if (matchingImageIds.isEmpty()) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        }

        // Get paginated image information entities
        Page<ImageInformationEntity> imageInfoPage = imageInformationRepository.findByIdIn(matchingImageIds, pageable);

        // Convert to ImageDto
        List<ImageDto> imageDtos = imageInfoPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(imageDtos, pageable, imageInfoPage.getTotalElements());
    }

    private List<Long> findMatchingImageIds(List<ImageRequestDto> filters) {
        // Start with all image information IDs
        List<Long> candidateIds = imageInformationRepository.findAll().stream()
                .map(ImageInformationEntity::getId)
                .collect(Collectors.toList());

        for (ImageRequestDto filter : filters) {
            Optional<ImagePredicateEntity> predicateOpt = imagePredicateRepository.findByPredicateAndValue(filter.predicateName(), filter.value());
            if (predicateOpt.isPresent()) {
                Long matchingId = predicateOpt.get().getImageInformation().getId();
                if (!candidateIds.contains(matchingId)) {
                    candidateIds.clear();
                    break;
                }
                candidateIds = Collections.singletonList(matchingId);
            } else {
                candidateIds.clear();
                break;
            }
        }

        return candidateIds;
    }

    private ImageDto convertToDto(ImageInformationEntity entity) {
        ImageInformationDto infoDto = new ImageInformationDto(
                entity.getFilename(),
                entity.getHash(),
                entity.getWidth(),
                entity.getHeight()
        );

        List<ImagePredicateDto> predicateDtos = entity.getPredicates().stream()
                .map(p -> new ImagePredicateDto(p.getPredicate(), p.getValue()))
                .collect(Collectors.toList());

        // For now, return empty byte array for image data
        // In a real implementation, you'd load the actual image data
        byte[] imageData = new byte[0];

        return new ImageDto(infoDto, predicateDtos, imageData);
    }

    private ImageDto convertToDto(Image image) {
        // This is a fallback for when we have Image entities instead of ImageInformationEntity
        // You might need to adjust based on your actual data model
        ImageInformationDto infoDto = new ImageInformationDto(
                image.getFilename(),
                "", // hash not available
                0,  // width not available
                0   // height not available
        );

        List<ImagePredicateDto> predicateDtos = java.util.Collections.emptyList(); // no predicates available

        byte[] imageData = new byte[0];

        return new ImageDto(infoDto, predicateDtos, imageData);
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

    public List<String> updateInventory() throws IOException {
        List<Path> files = fileService.listImageFilesInImagedir();
        files.forEach(this::updateImageInformation);
        return files.stream().map(p -> p.getFileName().toString()).collect(Collectors.toList());
    }

    public void updateImageInformation(Path imagePath) {
        String filename = imagePath.toFile().getAbsolutePath();
        Optional<ImageInformationEntity> knownImageInformation = imageInformationRepository.findByFilename(filename);
        File imageFile = imagePath.toFile();
        if(knownImageInformation.isEmpty()){
            ImageInformationEntity fileInformationEntity = new ImageInformationEntity();
            String hash = FileUtil.calculateFileHash(imageFile);
            fileInformationEntity.setFilename(filename);
            fileInformationEntity.setHash(hash);
            ImageSize size = FileUtil.extractSize(imageFile);
            if (size != null) {
                fileInformationEntity.setWidth(size.widthInPx());
                fileInformationEntity.setHeight(size.heightInPx());
            }
            imageInformationRepository.save(fileInformationEntity);
        }
    }
}