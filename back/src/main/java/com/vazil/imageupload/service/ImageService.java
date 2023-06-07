package com.vazil.imageupload.service;

import com.vazil.imageupload.dto.ImageFile;
import com.vazil.imageupload.repository.ImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageService {
    private ImageRepository imageRepository;

    public ImageFile getById(String id) {
        return imageRepository.findImageFileById(id);
    }
}
