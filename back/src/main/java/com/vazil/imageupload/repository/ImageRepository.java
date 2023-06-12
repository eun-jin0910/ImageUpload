package com.vazil.imageupload.repository;

import com.vazil.imageupload.dto.ImageFile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImageRepository extends ReactiveMongoRepository<ImageFile, String> {

    Flux<ImageFile> findByUserId(String userId);
    Mono<Long> count();
}
