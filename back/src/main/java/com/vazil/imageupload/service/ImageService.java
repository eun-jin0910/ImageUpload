package com.vazil.imageupload.service;

import com.vazil.imageupload.dto.ImageFile;
import com.vazil.imageupload.repository.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Log4j2
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }


    public Mono<ImageFile> getImage(String id) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("=== " + now + " getById-service " + "===");

        return imageRepository.findById(id);
    }

    public Flux<ImageFile> getImageByUserId(String userId) {
        System.out.println("getById-service");
        return imageRepository.findByUserId(userId);
    }

    public Flux<ImageFile> getAllImages() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("=== " + now + " getAll-service " + "===");
        return imageRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    public Mono<ImageFile> createImage(ResponseEntity<Mono<ImageFile>> entity) {
        Mono<ImageFile> imageFileMono = entity.getBody();
        log.info("imageFileMono : " + imageFileMono.toString());
        return imageFileMono.flatMap(imageRepository::save);
    }
}
