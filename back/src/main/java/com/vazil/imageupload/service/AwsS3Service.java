package com.vazil.imageupload.service;

import lombok.RequiredArgsConstructor;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3;

    public String upload(MultipartFile multipartFile) throws IOException {
        String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename(); // 파일 이름 생성
        ObjectMetadata objMeta = new ObjectMetadata(); // S3에 업로드되는 객체의 메타데이터 설정
        objMeta.setContentLength(multipartFile.getInputStream().available()); // 파일 크기 설정
        amazonS3.putObject(bucket, s3FileName, multipartFile.getInputStream(), objMeta); // S3 버킷에 파일 업로드
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }
}
