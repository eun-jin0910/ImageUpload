package com.vazil.imageupload.dto;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ImageFile {

    @Id
    private ObjectId _id;
    private String id;
    private String userId;
    private String fileName;
    private String saveFileName;
    private String fileURL;
    private String contentType;
    private long size;

}
