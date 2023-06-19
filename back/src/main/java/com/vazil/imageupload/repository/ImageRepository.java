package com.vazil.imageupload.repository;

import com.vazil.imageupload.model.ImageFile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ImageRepository extends ReactiveMongoRepository<ImageFile, String> {
}
