package com.github.martinfrank.imageorganiser.imageorganiser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImageInformationRepository  extends JpaRepository<ImageInformationEntity, Long> {

    @Query("SELECT i FROM ImageInformationEntity i WHERE i.hash = ?1 OR i.filename = ?2")
    Optional<ImageInformationEntity> findByHashOrFileName(String hash, String filename);

    @Query("SELECT i FROM ImageInformationEntity i WHERE i.filename = ?1")
    Optional<ImageInformationEntity> findByFilename(String filename);

    @Query("SELECT i FROM ImageInformationEntity i WHERE i.id IN :ids")
    Page<ImageInformationEntity> findByIdIn(List<Long> ids, Pageable pageable);
}
