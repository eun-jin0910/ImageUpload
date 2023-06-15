package com.vazil.imageupload.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
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

    public Mono<String> upload(FilePart filePart) {
        log.info("***AwsS3Service-upload***");
        String s3FileName = UUID.randomUUID() + "-" + filePart.filename();
        log.info("s3FileName: " + s3FileName);

        return copyToFilePart(filePart)
                .flatMap(tempFile -> {
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3FileName)
                            .build();
                    return Mono.fromCompletionStage(() -> amazonS3.putObject(putObjectRequest, AsyncRequestBody.fromFile(tempFile)))
                            .thenReturn(amazonS3.utilities().getUrl(builder -> builder.bucket(bucket).key(s3FileName)).toExternalForm());
                });
    }

    public Mono<Path> copyToFilePart(FilePart filePart) {
        try {
            Path tempFile = Files.createTempFile("temp", "file");
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(tempFile, StandardOpenOption.WRITE);
            return filePart
                    .transferTo(tempFile.toFile())
                    .doFinally(signalType -> {
                        try {
                            channel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .thenReturn(tempFile);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }
}
