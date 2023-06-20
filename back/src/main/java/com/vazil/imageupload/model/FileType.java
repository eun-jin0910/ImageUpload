package com.vazil.imageupload.model;

import lombok.ToString;

@ToString
public enum FileType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    BMP("image/bmp"),
    TIFF("image/tiff"),
    WebP("image/webp"),
    SVG("image/svg+xml");

    private String imageType;

    private FileType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageType() {
        return imageType;
    }

    public static FileType fromImageType(String imageType) {
        for (FileType fileType : FileType.values()) {
            if (fileType.getImageType().equals(imageType)) {
                return fileType;
            }
        }
        return null;
    }
}
