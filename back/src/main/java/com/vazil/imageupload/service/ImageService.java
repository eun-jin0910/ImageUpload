package com.vazil.imageupload.service;

import com.vazil.imageupload.dto.ChunkRequestDTO;
import com.vazil.imageupload.dto.ImageRequestDTO;
import com.vazil.imageupload.dto.ImageResponseDTO;
import com.vazil.imageupload.model.FileType;
import com.vazil.imageupload.model.ImageFile;
import com.vazil.imageupload.repository.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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


    // 분할 업로드
    public Mono<String> uploadImageChunk(Mono<ChunkRequestDTO> chunkDTOMono) {
        return chunkDTOMono.flatMap(chunkDTO -> {
            log.info("업로드 시작");
            log.info(chunkDTO.toString());
            String tempPath = "C:/path/to/chunk/";
            String outputPath = "C:/path/to/output/" + chunkDTO.getFileName();
            // 파일 저장

            File tempFile = null;
            try {
                File tempDirectory = new File(tempPath);
                if (!tempDirectory.exists()) {
                    if (tempDirectory.mkdirs()) {
                        log.info("임시 파일 폴더가 생성되었습니다.");
                    } else {
                        log.error("임시 파일 폴더를 생성하는데 실패하였습니다.");
                        return Mono.error(new RuntimeException("임시 파일 폴더 생성 실패"));
                    }
                }


                tempFile = File.createTempFile("temp_" + chunkDTO.getIndex() + "_", chunkDTO.getFileName(), new File(tempPath));
                log.info("임시 파일 경로: " + tempFile.getAbsolutePath());

                return chunkDTO.getChunk().transferTo(tempFile)
                        .then(Mono.defer(() -> {
                            if (chunkDTO.getIndex().equals(chunkDTO.getEnd())) {
                                log.info("이미지 병합");
                                // 분할된 이미지 파일 경로 다 가져오기
                                try {
                                    List<Path> chunkPaths = Files.list(Paths.get(tempPath)).collect(Collectors.toList());
                                    log.info("분할 파일 경로 : " + chunkPaths);

                                    BufferedImage mergedImage = null;

                                    for (Path chunkPath : chunkPaths) {
                                        BufferedImage chunkImage = ImageIO.read(chunkPath.toFile());

                                        if (mergedImage == null) {
                                            log.info("여긴 언제");
                                            // 첫 번째 청크 파일인 경우 병합할 이미지 생성
                                            mergedImage = new BufferedImage(chunkImage.getWidth(), chunkImage.getHeight(), chunkImage.getType());
                                        }
                                        // 청크 이미지를 병합할 이미지에 추가
                                        mergedImage.getGraphics().drawImage(chunkImage, 0, 0, null);
                                    }

                                    // 병합된 이미지를 저장
                                    String extension = chunkDTO.getFileName().substring(chunkDTO.getFileName().lastIndexOf(".") + 1);
                                    ImageIO.write(mergedImage, extension, new File(outputPath));

                                    System.out.println("이미지 병합 완료");


                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } finally {
                                    // 파일 삭제
//                                    tempFile.deleteOnExit();
                                }

                                // 이미지 병합하기

                                // 새로운 이미지 생성

                                // 임시 파일들 삭제 - tempFile.deleteOnExit();

                                // 이미지 복사하여 파일 형태 변환

                                // 업로드 처리

                                return Mono.just("이미지 병합");
                            } else {
                                return Mono.just("임시 파일로 복사");
                            }
                        }));
            } catch (IOException e) {
                return Mono.error(new RuntimeException(e));
            }
        });
    }







    // 이미지 처리할때 파일 손상됨 --실패

    public Mono<String> uploadImage(Mono<FilePart> filePartMono, String fileName, String index, String endIndex, String password, String title) {
        log.info("***ImageService-uploadImage***");
        log.info("업로드시작");
        int end = Integer.parseInt(endIndex);
        int count = Integer.parseInt(index);
        return filePartMono
                .flatMap(chunkPart -> {
                    return saveChunk(chunkPart, fileName, index)
                            .flatMap(path -> concatChunk(path, fileName, end, count));
                })
                .flatMap(concatenatedImage -> {
                    if (concatenatedImage == null) {
                        return Mono.error(new RuntimeException("파일 없음"));
                    }
                    FilePart filePart = createFilePart(concatenatedImage.toFile(), fileName);
                    ImageRequestDTO imageDTO = new ImageRequestDTO(title, password, filePart);
                    return createImage(Mono.just(imageDTO))
                            .map(result -> {
                                try {
                                    Files.deleteIfExists(concatenatedImage);
                                } catch (IOException e) {
                                    log.error("파일 삭제 실패", concatenatedImage, e);
                                }
                                return concatenatedImage.toString();
                            });
                });
    }

    private Mono<Path> saveChunk(FilePart filePart, String fileName, String index) {
        log.info("***ImageService-saveImage***");
        log.info(index + "번째 청크 파일 저장중");
        try {
            Path chunkDirectory = Paths.get("/tmp/chunk/");
            if (!Files.exists(chunkDirectory)) {
                Files.createDirectories(chunkDirectory);
            }
            String uniqueFileName = UUID.randomUUID().toString();
            Path tempFile = chunkDirectory.resolve(uniqueFileName);
            return filePart.transferTo(tempFile).thenReturn(tempFile);

//            Path tempFile = chunkDirectory.resolve(fileName.replace(".", "") + index + fileName);
//            return filePart.transferTo(tempFile).thenReturn(tempFile);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }


    private Mono<Path> concatChunk(Path path, String fileName, int end, int count) {
        log.info("***ImageService-concatImage***");
        if(count == end) {
            log.info("병합 시작");
            String outputPath = "C:/path/to/output/folder/" + fileName;

            Path outputFile = Paths.get(outputPath);
            if (Files.notExists(outputFile.getParent())) {
                try {
                    Files.createDirectories(outputFile.getParent());
                } catch (IOException e) {
                    return Mono.error(e);
                }
            }
            log.info("파일 경로 : " + outputFile);
            return Mono.fromCallable(() -> {
                        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(outputFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                            log.info("청크 파일 경로 : " + path);
                            Files.copy(path, out);
                            Files.deleteIfExists(path);
                        }
                        return path;
                    })
                    .then(Mono.just(outputFile));
        } else {
            return Mono.empty();
        }
    }

    private FilePart createFilePart(File file, String name) {
        log.info("***ImageService-createFilePart***");
        log.info("File -> FilePart");
        String filename = file.getName();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(name, filename);
        Resource resource = new FileSystemResource(file);
        return new FilePart() {
            @Override
            public String name() {
                return name;
            }
            @Override
            public HttpHeaders headers() {
                return headers;
            }
            @Override
            public Flux<DataBuffer> content() {
                return DataBufferUtils.read(resource, new DefaultDataBufferFactory(), 4096);
            }
            @Override
            public String filename() {
                return filename;
            }
            @Override
            public Mono<Void> transferTo(Path dest) {
                return Mono.fromCallable(() -> {
                    Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                    return null;
                });
            }
        };
    }










}
