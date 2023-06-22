package com.vazil.imageupload.controller;

import com.vazil.imageupload.dto.ChunkRequestDTO;
import com.vazil.imageupload.dto.ImageRequestDTO;
import com.vazil.imageupload.dto.ImageResponseDTO;

import com.vazil.imageupload.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@Log4j2
public class ImageController {

    @Autowired
    ImageService imageService;

    @GetMapping("/image/{id}")
    public Mono<ImageResponseDTO> selectImageById(@PathVariable(value = "id") String id) {
        log.info("***ImageController-selectImageById***");
        return imageService.getImageById(id);
    }

    @GetMapping("/images")
    public Flux<ImageResponseDTO> selectAllImages() {
        log.info("***ImageController-selectAllImages***");
        return imageService.getAllImages();
    }


    // 일괄,순차,병렬 업로드

//    @PostMapping(value = "/image", consumes = "multipart/form-data")
//    public Flux<String> insertImage(@RequestPart("images") Flux<FilePart> filePartFlux,
//                                    @RequestPart("password") String password,
//                                    @RequestPart("title") String title) {
//        log.info("***ImageController-insertImage***");
//        return filePartFlux.flatMap(filePart -> {
//            ImageRequestDTO imageDTO = new ImageRequestDTO(title, password, filePart);
//            return imageService.createImage(Mono.just(imageDTO));
//        });
//    }


    // 분할 업로드

    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public Mono<String> insertImage(@RequestPart("image") Mono<FilePart> filePartMono,
                                    @RequestPart("fileName") String fileName,
                                    @RequestPart("index") String index,
                                    @RequestPart("end") String end,
                                    @RequestPart("password") String password,
                                    @RequestPart("title") String title) {
        log.info("***ImageController-insertImage***");
        return filePartMono.flatMap(filePart -> {
            ChunkRequestDTO chunkDTO = new ChunkRequestDTO(title, password, fileName, index, end, filePart);
            return imageService.uploadImageChunk(Mono.just(chunkDTO));
        });
    }

    @PatchMapping( "/image")
    public Mono<String> updateImagePatch(@RequestPart("id") String id,
                                         @RequestPart("password") String password,
                                         @RequestPart("title") String title) {
        log.info("***ImageController-updateImagePatch***");
        return imageService.updateImagePatch(id, password, title);
    }

    @PutMapping(value = "/image", consumes = "multipart/form-data")
    public Mono<String> updateImagePut(@RequestPart("id") String id,
                                       @RequestPart("image") Mono<FilePart> filePartMono,
                                       @RequestPart("password") String password,
                                       @RequestPart("title") String title) {
        log.info("***ImageController-updateImagePut***");
        return filePartMono.flatMap(filePart -> {
            ImageRequestDTO imageDTO = new ImageRequestDTO(title, password, filePart);
            return imageService.updateImagePut(id, Mono.just(imageDTO));
        });
    }

    @DeleteMapping(value = "/image")
    public Mono<Void> deleteImage(@RequestBody Map<String, String> request) {
        log.info("***ImageController-deleteImage***");
        String id = request.get("id");
        String password = request.get("password");
        return imageService.deleteImageById(id, password);
    }

    @DeleteMapping(value = "/images")
    public Mono<Void> deleteAllImages() {
        log.info("***ImageController-deleteAllImages***");
        return imageService.deleteAllImages();
    }
}
