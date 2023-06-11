package com.vazil.imageupload.controller;

import com.vazil.imageupload.dto.ImageFile;

import com.vazil.imageupload.service.AwsS3Service;
import com.vazil.imageupload.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@Log4j2
public class ImageController {

    @Autowired
    ImageService imageService;
    @Autowired
    AwsS3Service awsS3Service;

    @GetMapping("/image")
    public Mono<ResponseEntity<ImageFile>> selectImage() {
        LocalDateTime now = LocalDateTime.now();
        log.info("=== " + now + " selectImage-controller " + "===");
        String _id = "647fd9a985f3b776565b6094";
        Mono<ResponseEntity<ImageFile>> img = imageService.getById(_id)
                .map(imageFile -> ResponseEntity.ok(imageFile))
                .defaultIfEmpty(ResponseEntity.notFound().build());
        return img;
    }

    @GetMapping("/image/{userId}")
    public Mono<Map<String, ImageFile>> selectImageByUserId(@PathVariable(value = "userId") String userId) {
        log.info("selectImageByUserId-controller");
        Flux<ImageFile> imageFileFlux = imageService.getByUserId(userId);
        return imageFileFlux
                .collectMap(ImageFile::getFileURL);
    }

    @GetMapping("/images")
    public Mono<Map<String, ImageFile>> selectAllImages() {
        log.info("selectAllImages-controller");
        Flux<ImageFile> imageFileFlux = imageService.getAll();
        return imageFileFlux
                .collectMap(ImageFile::getFileURL);
    }

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public Mono<String> insertImage(ServerWebExchange exchange,
                                    @RequestPart("image") Mono<FilePart> filePartMono,
                                    @RequestPart("uploaderName") String uploaderName,
                                    @RequestPart("imageTitle") String imageTitle) {
        LocalDateTime now = LocalDateTime.now();
        log.info("=== " + now + " insertImage-controller " + "===");

        return filePartMono.flatMap(filePart -> {
            try {
                if (filePart != null) {
                    String imageURL = awsS3Service.upload(filePart);
                    System.out.println("imageURL : " + imageURL);
                    System.out.println("uploaderName : " + uploaderName);
                    System.out.println("imageTitle : " + imageTitle);
                    return Mono.just("File uploaded successfully");
                } else {
                    throw new IllegalArgumentException("File part is null");
                }
            } catch (IOException e) {
                return Mono.error(e);
            }
        }).onErrorResume(e -> {
            log.error("Error occurred during image upload", e);
            return Mono.just("File upload failed: " + e.getMessage());
        });
    }


    @PatchMapping("/image")
    public void updateImagePatch() {

    }

    @PutMapping("/image")
    public void updateImagePut() {

    }

    @DeleteMapping("/image")
    public void deleteImage() {

    }
}
