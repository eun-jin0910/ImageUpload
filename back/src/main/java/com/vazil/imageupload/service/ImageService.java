package com.vazil.imageupload.service;

import com.vazil.imageupload.model.FileType;
import com.vazil.imageupload.model.ImageFile;
import com.vazil.imageupload.repository.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@Log4j2
public class ImageService {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    AwsS3Service awsS3Service;

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

    public Mono<Boolean> verifyUserPw(String id, String password) {
        return imageRepository.findById(id)
                .map(image -> image.getPassword().equals(password))
                .defaultIfEmpty(false);
    }

    @Transactional
    public Flux<String> createImage(Flux<FilePart> filePartFlux, String password, String title) {
        log.info("***ImageService-createImage***");
        LocalDateTime now = LocalDateTime.now();
        filePartFlux.count().subscribe(count -> log.info("전송된 파일 개수: " + count));
        return filePartFlux.switchIfEmpty(Mono.error(new IllegalArgumentException("이미지 파일 없음")))
                .flatMapSequential(filePart -> awsS3Service.upload(filePart)
                        .flatMap(fileURL -> {
                            FileType fileType = FileType.fromImageType(filePart.headers().getContentType().toString());
                            ImageFile imageFile = ImageFile.builder()
                                    .fileType(fileType)
                                    .password(password)
                                    .fileName(filePart.filename())
                                    .title(title)
                                    .uploadDate(now)
                                    .fileURL(fileURL)
                                    .build();
                            return imageRepository.save(imageFile);
                        }))
                .onErrorResume(e -> {
                    log.error("이미지 업로드 실패", e);
                    return Mono.error(new RuntimeException("이미지 업로드 실패: " + e.getMessage()));
                })
                .map(imageFile -> "업로드 성공");
    }

    @Transactional
    public Mono<ImageFile> updateImagePatch(String id, String password, String title, String fileName, String fileURL, FileType fileType) {
        log.info("***ImageService-updateImageById***");
        LocalDateTime now = LocalDateTime.now();
        return verifyUserPw(id, password)
                .flatMap(ok -> {
                    if (ok) {
                        return imageRepository.findById(id)
                                .flatMap(image -> {
                                    image.setFileName(fileName);
                                    image.setUploadDate(now);
                                    image.setTitle(title);
                                    image.setFileURL(fileURL);
                                    image.setFileType(fileType);
                                    return imageRepository.save(image);
                                });
                    } else {
                        return Mono.error(new Exception("비밀번호가 일치하지 않습니다."));
                    }
                })
                .onErrorResume(e -> {
                    log.error("이미지 업데이트 실패", e);
                    return Mono.error(new Exception("이미지 업데이트 실패: " + e.getMessage()));
                });
    }

    @Transactional
    public Mono<ImageFile> updateImagePut(String id, Mono<FilePart> filePartMono, String password, String title) {
        log.info("***ImageService-updateImage***");
        LocalDateTime now = LocalDateTime.now();
        return verifyUserPw(id, password)
                .flatMap(ok -> {
                    if (ok) {
                        return filePartMono.flatMap(filePart -> {
                            if (filePart != null) {
                                return awsS3Service.upload(filePart)
                                        .flatMap(fileURL -> {
                                            FileType fileType = FileType.fromImageType(filePart.headers().getContentType().toString());
                                            return imageRepository.findById(id)
                                                    .flatMap(image -> {
                                                        image.setFileName(filePart.filename());
                                                        image.setUploadDate(now);
                                                        image.setTitle(title);
                                                        image.setFileURL(fileURL);
                                                        image.setFileType(fileType);
                                                        return imageRepository.save(image);
                                                    });
                                        });
                            } else {
                                return Mono.error(new Exception("이미지 파일이 없습니다."));
                            }
                        });
                    } else {
                        return Mono.error(new Exception("비밀번호가 일치하지 않습니다."));
                    }
                })
                .onErrorResume(e -> {
                    log.error("이미지 업데이트 실패", e);
                    return Mono.error(new Exception("이미지 업데이트 실패: " + e.getMessage()));
                });
    }

    @Transactional
    public Mono<Void> deleteImageById(String id, String password) {
        log.info("***ImageService-deleteImageById***");
        return verifyUserPw(id, password).flatMap(ok -> {
            if (ok) {
                return imageRepository.deleteById(id);
            } else {
                return Mono.error(new Exception("비밀번호가 일치하지 않습니다."));
            }
        }).onErrorResume(e -> {
            log.error("이미지 삭제 실패", e);
            return Mono.error(new Exception("이미지 삭제 실패: " + e.getMessage()));
        });
    }
}
