package com.vazil.imageupload.dto;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "imageFile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ImageFile {

    @Id
    private String id;
    private String userId;
    private String userPw;
    private String fileName;
    private String title;
    private String fileURL;
    private String uploadDate;
    private String fileType;
}
