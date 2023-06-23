package com.vazil.imageupload.service;

import com.vazil.imageupload.dto.ImageRequestDTO;
import com.vazil.imageupload.dto.ImageResponseDTO;
import com.vazil.imageupload.model.FileType;
import com.vazil.imageupload.model.ImageFile;
import com.vazil.imageupload.repository.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public Mono<ImageResponseDTO> getImageById(String id) {
        log.info("***ImageService-getImageById***");
        return imageRepository.findById(id)
                .map(imageFile -> new ImageResponseDTO(id, imageFile.getTitle(), imageFile.getFileName(), imageFile.getFileURL(), imageFile.getFileType(), imageFile.getUploadDate()));
    }

    public Flux<ImageResponseDTO> getAllImages() {
        log.info("***ImageService-getAllImages***");
        return imageRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadDate"))
                .map(imageFile -> new ImageResponseDTO(imageFile.getId(), imageFile.getTitle(), imageFile.getFileName(), imageFile.getFileURL(), imageFile.getFileType(), imageFile.getUploadDate()));
    }

    public Mono<Boolean> verifyUserPw(String id, String password) {
        return imageRepository.findById(id)
                .map(image -> image.getPassword().equals(password))
                .defaultIfEmpty(false);
    }

    @Transactional
    public Mono<String> createImage(Mono<ImageRequestDTO> imageRequestDTOMono) {
        log.info("***ImageService-createImage***");
        LocalDateTime now = LocalDateTime.now();
        return imageRequestDTOMono.flatMap(imageRequestDTO -> {
            return awsS3Service.upload(imageRequestDTO.getImage())
                    .flatMap(fileURL -> {
                        FileType fileType = FileType.fromImageType(imageRequestDTO.getImage().headers().getContentType().toString());
                        ImageFile imageFile = ImageFile.builder()
                                .fileType(fileType)
                                .password(imageRequestDTO.getPassword())
                                .fileName(imageRequestDTO.getImage().filename())
                                .title(imageRequestDTO.getTitle())
                                .uploadDate(now)
                                .fileURL(fileURL)
                                .build();
                        return imageRepository.save(imageFile);
                    })
                    .onErrorResume(e -> {
                        log.error("이미지 업로드 실패", e);
                        return Mono.error(new RuntimeException("이미지 업로드 실패: " + e.getMessage()));
                    })
                    .map(imageFile -> "업로드 성공");
        });
    }

    @Transactional
    public Mono<String> updateImagePut(String id, Mono<ImageRequestDTO> imageRequestDTOMono) {
        log.info("***ImageService-updateImage***");
        LocalDateTime now = LocalDateTime.now();
        return imageRequestDTOMono.flatMap(imageRequestDTO -> {
            return verifyUserPw(id, imageRequestDTO.getPassword())
                    .flatMap(ok -> {
                        if (ok) {
                            if (imageRequestDTO.getImage() != null) {
                                return awsS3Service.upload(imageRequestDTO.getImage())
                                        .flatMap(fileURL -> {
                                            FileType fileType = FileType.fromImageType(imageRequestDTO.getImage().headers().getContentType().toString());
                                            return imageRepository.findById(id)
                                                    .flatMap(image -> {
                                                        image.setFileName(imageRequestDTO.getImage().filename());
                                                        image.setTitle(imageRequestDTO.getTitle());
                                                        image.setUploadDate(now);
                                                        image.setFileURL(fileURL);
                                                        image.setFileType(fileType);
                                                        return imageRepository.save(image)
                                                                .map(updatedImage -> "이미지 업데이트 성공");
                                                    });
                                        });
                            } else {
                                return Mono.error(new Exception("이미지 파일이 없습니다."));
                            }

                        } else {
                            return Mono.error(new Exception("비밀번호가 일치하지 않습니다."));
                        }
                    })
                    .onErrorResume(e -> {
                        log.error("이미지 업데이트 실패", e);
                        return Mono.error(new Exception("이미지 업데이트 실패: " + e.getMessage()));
                    });
        });
    }

    @Transactional
    public Mono<String> updateImagePatch(String id, String password, String title) {
        log.info("***ImageService-updateImageById***");
        LocalDateTime now = LocalDateTime.now();
        return verifyUserPw(id, password)
                .flatMap(ok -> {
                    if (ok) {
                        return imageRepository.findById(id)
                                .flatMap(image -> {
                                    image.setUploadDate(now);
                                    image.setTitle(title);
                                    return imageRepository.save(image)
                                            .map(updatedImage -> "이미지 업데이트 성공");
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

    @Transactional
    public Mono<Void> deleteAllImages() {
        log.info("***ImageService-deleteAllImages***");
        return imageRepository.deleteAll().then();
    }
}
