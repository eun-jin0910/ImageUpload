package com.vazil.imageupload.controller;

import com.vazil.imageupload.model.FileType;
import com.vazil.imageupload.model.ImageFile;

import com.vazil.imageupload.service.AwsS3Service;
import com.vazil.imageupload.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@CrossOrigin("*")
@RestController
@Log4j2
public class ImageController {

    @Autowired
    ImageService imageService;
    @Autowired
    AwsS3Service awsS3Service;

    @GetMapping("/image/{id}")
    public Mono<ResponseEntity<ImageFile>> selectImageById(@PathVariable(value = "id") String id) {
        log.info("***ImageController-selectImageById***");
        Mono<ResponseEntity<ImageFile>> imageFileMono = imageService.getImageById(id)
                .map(imageFile -> ResponseEntity.ok(imageFile))
                .defaultIfEmpty(ResponseEntity.notFound().build());
        return imageFileMono;
    }

    @GetMapping("/images")
    public Flux<ImageFile> selectAllImages() {
        log.info("***ImageController-selectAllImages***");
        Flux<ImageFile> imageFileFlux = imageService.getAllImages()
                .filter(imageFile -> imageFile.getFileURL() != null);
        return imageFileFlux;
    }

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public Flux<String> insertImage(@RequestPart("images") Flux<FilePart> filePartFlux,
                                    @RequestPart("password") String password,
                                    @RequestPart("title") String title) {
        log.info("***ImageController-insertImage***");
        return imageService.createImage(filePartFlux, password, title);
    }

    @PatchMapping(value = "/image", consumes = "multipart/form-data")
    public Mono<ImageFile> updateImagePatch(@RequestPart("id") String id,
                                            @RequestPart("password") String password,
                                            @RequestPart("title") String title,
                                            @RequestPart("fileName") String fileName,
                                            @RequestPart("fileURL") String fileURL,
                                            @RequestPart("fileType") FileType fileType) {
        log.info("***ImageController-updateImagePatch***");
        return imageService.updateImagePatch(id, password, title, fileName, fileURL, fileType);
    }

    @PutMapping(value = "/image", consumes = "multipart/form-data")
    public Mono<ImageFile> updateImagePut(@RequestPart("id") String id,
                                          @RequestPart("image") Mono<FilePart> filePartMono,
                                          @RequestPart("password") String password,
                                          @RequestPart("title") String title) {
        log.info("***ImageController-updateImagePut***");
        return imageService.updateImagePut(id, filePartMono, password, title);
    }

    @DeleteMapping(value = "/image")
    public Mono<Void> deleteImage(@RequestBody Map<String, String> request) {
        log.info("***ImageController-deleteImage***");
        String id = request.get("id");
        String userPw = request.get("userPw");
        return imageService.deleteImageById(id, userPw);
    }
}
