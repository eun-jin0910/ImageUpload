package com.vazil.imageupload.dto;

import com.vazil.imageupload.model.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponseDTO {
    private String id;
    private String title;
    private String fileName;
    private String fileURL;
    private FileType fileType;
    private LocalDateTime uploadDate;
}
