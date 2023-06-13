package com.vazil.imageupload.controller;

import com.vazil.imageupload.dto.ImageFile;

import com.vazil.imageupload.service.AwsS3Service;
import com.vazil.imageupload.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
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
        log.info("***ImageController-selectImage***");
        String _id = "647fd9a985f3b776565b6094";
        Mono<ResponseEntity<ImageFile>> image = imageService.getImageById(_id)
                .map(imageFile -> ResponseEntity.ok(imageFile))
                .defaultIfEmpty(ResponseEntity.notFound().build());
        return image;
    }

    @GetMapping("/image/{userId}")
    public Mono<Map<String, ImageFile>> selectImageByUserId(@PathVariable(value = "userId") String userId) {
        log.info("***ImageController-selectImageByUserId***");
        Flux<ImageFile> imageFileFlux = imageService.getImageByUserId(userId);
        return imageFileFlux
                .collectMap(ImageFile::getFileURL);
    }

    @GetMapping("/image/{fileURL}")
    public Mono<ResponseEntity<ImageFile>> selectImageByFileURL(@PathVariable(value = "fileURL") String fileURL) {
        log.info("***ImageController-selectImageByFileURL***");
        Mono<ResponseEntity<ImageFile>> image = imageService.getImageByFileURL(fileURL)
                .map(imageFile -> ResponseEntity.ok(imageFile))
                .defaultIfEmpty(ResponseEntity.notFound().build());
        return image;
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
                                    @RequestPart("userId") String userId,
                                    @RequestPart("userPw") String userPw,
                                    @RequestPart("title") String title,
                                    @RequestPart("uploadDate") String uploadDate) {
        log.info("***ImageController-insertImage***");
        filePartFlux.count().subscribe(count -> log.info("전송된 파일 개수: " + count));
        return filePartFlux.flatMapSequential(filePart -> { // Flux의 각 요소 비동기 처리
            try {
                if (filePart != null) {
                    String fileURL = awsS3Service.upload(filePart); // AWS S3 업로드 => URL 반환
                    String fileType = filePart.headers().getContentType().toString();
                    ImageFile imageFile = ImageFile.builder()
                            .fileType(fileType)
                            .userId(userId)
                            .userPw(userPw)
                            .fileName(title)
                            .title(title)
                            .uploadDate(uploadDate)
                            .fileURL(fileURL)
                            .build();
                    Mono<ImageFile> imageFileMono = Mono.just(imageFile);
                    ResponseEntity<Mono<ImageFile>> entity = ResponseEntity.ok(imageFileMono);
                    imageService.createImage(entity).subscribe();
                    return Mono.just("업로드 성공").flux();
                } else {
                    return Flux.error(new IllegalArgumentException("이미지 파일 없음"));
                }
            } catch (IOException e) {
                return Flux.error(e);
            }
        }).onErrorResume(e -> {
            log.error("이미지 업로드 실패", e);
            return Flux.just("File upload failed: " + e.getMessage());
        });
    }

    @PatchMapping("/image")
    public void updateImagePatch() {

    }

    @PutMapping("/image")
    public void updateImagePut() {

    }
    @DeleteMapping(value = "/image")
    public Mono<Void> deleteImage(@RequestBody Map<String, String> request) {
        String fileURL = request.get("fileURL");
        String userPw = request.get("userPw");

        log.info("***ImageController-deleteImage***");
        log.info("fileURL: " + fileURL);
        log.info("userPw: " + userPw);

        return imageService.deleteImage(fileURL);
    }
}
