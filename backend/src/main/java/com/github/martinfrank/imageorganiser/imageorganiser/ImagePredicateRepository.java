package com.github.martinfrank.imageorganiser.imageorganiser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImagePredicateRepository extends JpaRepository<ImagePredicateEntity, Long> {

    @Query("SELECT p FROM ImagePredicateEntity p WHERE p.imageInformation.id = ?1")
    List<ImagePredicateEntity> findByImageInformationId(Long imageInformationId);

    @Query("SELECT p FROM ImagePredicateEntity p WHERE p.predicate = ?1 AND p.value = ?2")
    Optional<ImagePredicateEntity> findByPredicateAndValue(String predicate, String value);
}