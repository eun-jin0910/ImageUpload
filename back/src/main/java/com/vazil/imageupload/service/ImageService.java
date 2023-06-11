package com.vazil.imageupload.service;

import com.vazil.imageupload.dto.ImageFile;
import com.vazil.imageupload.repository.ImageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Mono<ImageFile> getById(String id) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("=== " + now + " getById-service " + "===");

        return imageRepository.findById(id);
    }

    public Flux<ImageFile> getByUserId(String userId) {
        System.out.println("getById-service");
        return imageRepository.findByUserId(userId);
    }

    public Flux<ImageFile> getAll() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("=== " + now + " getAll-service " + "===");
        return imageRepository.findAll();
    }

}
