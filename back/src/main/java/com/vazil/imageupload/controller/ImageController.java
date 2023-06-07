package com.vazil.imageupload.controller;

import com.vazil.imageupload.dto.ImageFile;

import com.vazil.imageupload.service.ImageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    ImageService imageService;

    @GetMapping("/image/{id}")
    public ImageFile getImage(@PathVariable(value = "id") String id) {
        System.out.println(imageService.getById(id));
        return imageService.getById(id);
    }
}
