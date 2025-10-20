package com.github.martinfrank.imageorganiser.imageorganiser;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

@Entity
public class ImagePredicateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String predicate;
    private String value;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "image_information_id")
    private ImageInformationEntity imageInformation;

    // Default constructor
    public ImagePredicateEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
        this.updatedAt = LocalDateTime.now();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ImageInformationEntity getImageInformation() {
        return imageInformation;
    }

    public void setImageInformation(ImageInformationEntity imageInformation) {
        this.imageInformation = imageInformation;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ImagePredicateEntity{" +
                "id=" + id +
                ", predicate='" + predicate + '\'' +
                ", value='" + value + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", imageInformation=" + (imageInformation != null ? imageInformation.getId() : null) +
                '}';
    }
}
