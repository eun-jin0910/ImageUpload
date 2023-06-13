package com.vazil.imageupload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final S3AsyncClient amazonS3;

    public String upload(FilePart filePart) throws IOException {
        log.info("***AwsS3Service-upload***");
        String s3FileName = UUID.randomUUID() + "-" + filePart.filename(); // 파일 이름 생성
        log.info("s3FileName : " + s3FileName);

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
        log.info("tempFile : " + tempFile);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(s3FileName)
                .build();
        amazonS3.putObject(putObjectRequest, AsyncRequestBody.fromFile(tempFile));
        return amazonS3.utilities().getUrl(builder -> builder.bucket(bucket).key(s3FileName)).toExternalForm();
    }
}
