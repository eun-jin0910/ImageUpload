package com.vazil.imageupload.service;

import com.vazil.imageupload.dto.ImageFile;
import com.vazil.imageupload.repository.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    public Mono<ImageFile> getImageById(String id) {
        log.info("***ImageService-getImage***");
        return imageRepository.findById(id);
    }

    public Flux<ImageFile> getImageByUserId(String userId) {
        log.info("***ImageService-getImageByUserId***");
        return imageRepository.findByUserId(userId);
    }
    public Mono<ImageFile> getImageByFileURL(String fileURL) {
        log.info("***ImageService-getImageByFileURL***");
        return imageRepository.findByFileURL(fileURL);
    }
    public Flux<ImageFile> getAllImages() {
        log.info("***ImageService-getAllImages***");
        return imageRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadDate"));
    }
    @Transactional
    public Mono<ImageFile> createImage(ResponseEntity<Mono<ImageFile>> entity) {
        log.info("***ImageService-createImage***");
        Mono<ImageFile> imageFileMono = entity.getBody();
        return imageFileMono.flatMap(imageRepository::save);
    }

    @Transactional
    public Mono<Void> deleteImage(String fileURL) {
        return imageRepository.deleteByFileURL(fileURL);
    }

}
