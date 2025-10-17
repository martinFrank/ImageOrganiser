package com.github.martinfrank.imageorganiser.imageorganiser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImageInformationRepository  extends JpaRepository<ImageInformationEntity, Long> {

    @Query("SELECT i FROM ImageInformationEntity i WHERE i.hash = ?1 OR i.filename = ?2")
    Optional<ImageInformationEntity> findByHashOrFileName(String hash, String filename);
}
