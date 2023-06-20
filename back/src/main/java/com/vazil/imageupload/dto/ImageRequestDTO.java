package com.vazil.imageupload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageRequestDTO {
    private String title;
    private String password;
    private FilePart image;
}
