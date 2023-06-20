package com.vazil.imageupload.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "imageFile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageFile {

    @Id
    private String id;
    private String title;
    private String password;
    private String fileName;
    private String fileURL;
    private FileType fileType;
    private LocalDateTime uploadDate;
}
