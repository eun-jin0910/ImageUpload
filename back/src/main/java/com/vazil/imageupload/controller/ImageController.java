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
                                    @RequestPart("userId") String userId,
                                    @RequestPart("userPw") String userPw,
                                    @RequestPart("title") String title,
                                    @RequestPart("uploadDate") String uploadDate) {
        log.info("***ImageController-insertImage***");
        filePartFlux.count().subscribe(count -> log.info("전송된 파일 개수: " + count));
        return filePartFlux.flatMapSequential(filePart -> {
            if (filePart != null) {
                return awsS3Service.upload(filePart)
                        .flatMap(fileURL -> {
                            String fileType = filePart.headers().getContentType().toString();
                            ImageFile imageFile = ImageFile.builder()
                                    .fileType(fileType)
                                    .userId(userId)
                                    .userPw(userPw)
                                    .fileName(filePart.filename())
                                    .title(title)
                                    .uploadDate(uploadDate)
                                    .fileURL(fileURL)
                                    .build();
                            ResponseEntity<Mono<ImageFile>> entity = ResponseEntity.ok(Mono.just(imageFile));
                            return imageService.createImage(entity)
                                    .thenReturn("업로드 성공");
                        });
            } else {
                return Mono.error(new IllegalArgumentException("이미지 파일 없음"));
            }
        }).onErrorResume(e -> {
            log.error("이미지 업로드 실패", e);
            return Mono.just("이미지 업로드 실패: " + e.getMessage());
        });
    }

    @PatchMapping(value = "/image", consumes = "multipart/form-data")
    public Mono<ImageFile> updateImagePatch(@RequestPart("id") String id,
                                            @RequestPart("userId") String userId,
                                            @RequestPart("userPw") String userPw,
                                            @RequestPart("title") String title,
                                            @RequestPart("fileName") String fileName,
                                            @RequestPart("fileURL") String fileURL,
                                            @RequestPart("fileType") String fileType,
                                            @RequestPart("uploadDate") String uploadDate) {
        log.info("***ImageController-updateImagePatch***");

        return imageService.verifyUserPw(id, userPw).flatMap(ok -> {
            if (ok) {
                ImageFile imageFile = ImageFile.builder()
                        .userId(userId)
                        .title(title)
                        .uploadDate(uploadDate)
                        .fileName(fileName)
                        .fileURL(fileURL)
                        .fileType(fileType)
                        .build();
                return imageService.updateImageById(id, imageFile);
            } else {
                return Mono.error(new Exception("비밀번호가 일치하지 않습니다."));
            }
        }).onErrorResume(e -> {
            log.error("이미지 업데이트 실패", e);
            return Mono.error(new Exception("이미지 업데이트 실패: " + e.getMessage()));
        });
    }

    @PutMapping(value = "/image", consumes = "multipart/form-data")
    public Mono<ImageFile> updateImagePut(@RequestPart("id") String id,
                                          @RequestPart("image") Mono<FilePart> filePartMono,
                                          @RequestPart("userId") String userId,
                                          @RequestPart("userPw") String userPw,
                                          @RequestPart("title") String title,
                                          @RequestPart("uploadDate") String uploadDate) {
        log.info("***ImageController-updateImagePut***");
        return imageService.verifyUserPw(id, userPw).flatMap(ok -> {
            if(ok) {
                return filePartMono.flatMap(filePart -> {
                    if(filePart != null) {
                        return awsS3Service.upload(filePart)
                                .flatMap(fileURL -> {
                                    String fileType = filePart.headers().getContentType().toString();
                                    ImageFile imageFile = ImageFile.builder()
                                            .fileType(fileType)
                                            .userId(userId)
                                            .fileName(filePart.filename())
                                            .title(title)
                                            .uploadDate(uploadDate)
                                            .fileURL(fileURL)
                                            .build();
                                    return imageService.updateImageById(id, imageFile);
                                });
                    } else {
                        return Mono.error(new Exception("이미지 파일이 없습니다."));
                    }
                }).onErrorResume(e -> {
                    log.error("이미지 업데이트 실패", e);
                    return Mono.error(new Exception("이미지 업데이트 실패: " + e.getMessage()));
                });
            } else {
                return Mono.error(new Exception("비밀번호가 일치하지 않습니다."));
            }
        }).onErrorResume(e -> {
            log.error("이미지 업데이트 실패", e);
            return Mono.error(new Exception("이미지 업데이트 실패: " + e.getMessage()));
        });
    }

    @DeleteMapping(value = "/image")
    public Mono<Void> deleteImage(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        String userPw = request.get("userPw");

        log.info("***ImageController-deleteImage***");
        log.info("id: " + id);
        log.info("userPw: " + userPw);

        return imageService.verifyUserPw(id, userPw).flatMap(ok -> {
            if(ok) {
                return imageService.deleteImageById(id);
            } else {
                return Mono.error(new Exception("비밀번호가 일치하지 않습니다."));
            }
        }).onErrorResume(e -> {
            log.error("이미지 삭제 실패", e);
            return Mono.error(new Exception("이미지 삭제 실패: " + e.getMessage()));
        });
    }
}
