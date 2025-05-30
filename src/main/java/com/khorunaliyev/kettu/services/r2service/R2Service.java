package com.khorunaliyev.kettu.services.r2service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2Service {
    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    public String upload(MultipartFile file) throws IOException {
        String key = UUID.randomUUID() + "-" + file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(file.getContentType()).acl(ObjectCannedACL.PUBLIC_READ).build(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    public String getFileUrl(String key){
        return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build()).toString();
    }

    public byte[] downloadFile(String key){
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try(ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request)){
           return s3Object.readAllBytes();
        }
        catch (IOException e){
            throw new RuntimeException("Failed to read object bytes from R2", e);
        }
    }
}
