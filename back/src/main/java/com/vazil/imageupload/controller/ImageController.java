package com.vazil.imageupload.controller;

import com.vazil.imageupload.dto.ImageFile;

import com.vazil.imageupload.service.AwsS3Service;
import com.vazil.imageupload.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@CrossOrigin("*")
@RestController
@Log4j2
public class ImageController {

    @Autowired
    ImageService imageService;
    AwsS3Service awsS3Service;

    @GetMapping("/image")
    public Mono<ResponseEntity<ImageFile>> selectImage() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("===" + now + " selectImage-controller " + "===");
        String _id = "647fd9a985f3b776565b6094";
        Mono<ResponseEntity<ImageFile>> img = imageService.getById(_id)
                .map(imageFile -> ResponseEntity.ok(imageFile))
                .defaultIfEmpty(ResponseEntity.notFound().build());
        return img;
    }

    @GetMapping("/image/{userId}")
    public Mono<Map<String, ImageFile>> selectImageByUserId(@PathVariable(value = "userId") String userId) {
        System.out.println("selectImageByUserId-controller");
        Flux<ImageFile> imageFileFlux = imageService.getByUserId(userId);
        return imageFileFlux
                .collectMap(ImageFile::getFileURL);
    }

    @GetMapping("/images")
    public Mono<Map<String, ImageFile>> selectAllImages() {
        System.out.println("selectAllImages-controller");
        Flux<ImageFile> imageFileFlux = imageService.getAll();
        return imageFileFlux
                .collectMap(ImageFile::getFileURL);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String insertImage(@RequestParam(value = "imageFile") MultipartFile multipartFile) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("===" + now + " insertImage-controller " + "===");
        String imageURL = awsS3Service.upload(multipartFile);
        System.out.println(imageURL);
        return imageURL;
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
