package com.github.martinfrank.imageorganiser.imageorganiser;

import java.util.List;

public record ImageDto(ImageInformationDto imagInformation, List<ImagePredicateDto> predicates, byte[] imageData) {
}
