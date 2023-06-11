package com.vazil.imageupload.service;

import lombok.RequiredArgsConstructor;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3;

    public String upload(FilePart filePart) throws IOException {
        System.out.println("AwsS3Service-upload 시작");
        String s3FileName = UUID.randomUUID() + "-" + filePart.filename(); // 파일 이름 생성
        System.out.println("s3FileName : " + s3FileName);
        ObjectMetadata objMeta = new ObjectMetadata(); // S3에 업로드되는 객체의 메타데이터 설정
        objMeta.setContentLength(filePart.headers().getContentLength()); // 파일 크기 설정

        Path tempFile = Files.createTempFile("temp", "file");
        AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);

        // FilePart를 임시 파일로 쓰기
        filePart.transferTo(tempFile.toFile())
                .doOnSuccess(uploaded -> {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .subscribe();

        // 임시 파일로부터 InputStream 얻기
        InputStream inputStream = Files.newInputStream(tempFile, StandardOpenOption.READ);
        System.out.println("inputStream : " + inputStream);
        System.out.println("objMeta : " + objMeta);
        amazonS3.putObject(bucket, s3FileName, inputStream, objMeta); // S3 버킷에 파일 업로드
        return amazonS3.getUrl(bucket, s3FileName).toString();
    }
}
