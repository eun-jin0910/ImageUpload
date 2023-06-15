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
import reactor.core.scheduler.Schedulers;

@Service
@Log4j2
public class ImageService {
    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Mono<ImageFile> getImageById(String id) {
        log.info("***ImageService-getImageById***");
        return imageRepository.findById(id);
    }

    public Flux<ImageFile> getAllImages() {
        log.info("***ImageService-getAllImages***");
        return imageRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    public Mono<Boolean> verifyUserPw(String id, String userPw) {
        return imageRepository.findById(id)
                .map(image -> image.getUserPw().equals(userPw))
                .defaultIfEmpty(false);
    }

    @Transactional
    public Mono<ImageFile> createImage(ResponseEntity<Mono<ImageFile>> entity) {
        log.info("***ImageService-createImage***");
        Mono<ImageFile> imageFileMono = entity.getBody();
        return imageFileMono.flatMap(imageRepository::save);
    }

    @Transactional
    public Mono<Void> deleteImageById(String id) {
        log.info("***ImageService-deleteImageById***");
        return imageRepository.deleteById(id);
    }

    @Transactional
    public Mono<ImageFile> updateImageById(String id, ImageFile updatedImage) {
        log.info("***ImageService-updateImageById***");
        return imageRepository.findById(id)
                .flatMap(image -> {
                    image.setUserId(updatedImage.getUserId());
                    image.setUploadDate(updatedImage.getUploadDate());
                    image.setTitle(updatedImage.getTitle());
                    image.setUploadDate(updatedImage.getUploadDate());
                    image.setFileURL(updatedImage.getFileURL());
                    image.setFileType(updatedImage.getFileType());
                    return imageRepository.save(image);
                })
                .subscribeOn(Schedulers.parallel()); // 비동기 처리
    }
}
