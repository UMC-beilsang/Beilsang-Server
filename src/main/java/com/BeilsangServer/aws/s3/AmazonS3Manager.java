package com.BeilsangServer.aws.s3;

import com.BeilsangServer.config.AmazonConfig;
import com.BeilsangServer.domain.uuid.entity.Uuid;
import com.BeilsangServer.domain.uuid.repository.UuidRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.activation.MimetypesFileTypeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;
    private final AmazonConfig amazonConfig;
    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(getContentType(file));
        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String getContentType(MultipartFile file) {
        String contentType = new MimetypesFileTypeMap().getContentType(file.getOriginalFilename());
        return contentType != null ? contentType : "application/octet-stream";
    }

    public String generateMainKeyName(Uuid uuid) {
        return amazonConfig.getMainPath() + '/' + uuid.getUuid();
    }

    public String generateCertKeyName(Uuid uuid) {
        return amazonConfig.getCertPath() + '/' + uuid.getUuid();
    }

    public String generateFeedKeyName(Uuid uuid) {
        return amazonConfig.getFeedPath() + '/' + uuid.getUuid();
    }
}
