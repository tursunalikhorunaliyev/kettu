package com.khorunaliyev.kettu.services.storage;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.place.UserActiveUploads;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.place.UserActiveUploadsRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class R2Service {
    private final S3Client s3Client;
    private final UserActiveUploadsRepository activeUploadsRepository;
    private final PlaceRepository placeRepository;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    public ResponseEntity<Response> upload(MultipartFile image) {
        String key = UUID.randomUUID() + "-" + image.getOriginalFilename();
        try {
            s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(image.getContentType()).acl(ObjectCannedACL.PUBLIC_READ).build(), RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Something went wrong while file uploading", null), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(new Response("Success", key), HttpStatus.CREATED);
    }
    public ResponseEntity<Response> uploadMultiple(List<MultipartFile> images) {
        List<String> imagesList = new LinkedList<>();

        for (MultipartFile image : images) {
            String contentType = image.getContentType();
            if (contentType != null && contentType.startsWith("image/")) {
                String key = UUID.randomUUID() + "-" + image.getOriginalFilename();
                try {
                    s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(image.getContentType()).acl(ObjectCannedACL.PUBLIC_READ).build(), RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
                } catch (IOException e) {
                    return new ResponseEntity<>(new Response("Something went wrong while uploading file", null), HttpStatus.CONFLICT);
                }
                imagesList.add(key);
            }
        }

        return new ResponseEntity<>(new Response("Success", imagesList), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> deleteFile(String fileName) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build());
            return ResponseEntity.ok(new Response("Deleted", null));
        } catch (S3Exception e) {
            return new ResponseEntity<>(new Response("Something went wrong while deleting file", null), HttpStatus.CONFLICT);
        }
    }

    public ResponseEntity<Response> deleteFiles(List<String> fileNames) {

        for (String fileName : fileNames) {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build());
                return ResponseEntity.ok(new Response("Deleted", null));
            } catch (S3Exception e) {
                return new ResponseEntity<>(new Response("Something went wrong while deleting file", null), HttpStatus.CONFLICT);
            }
        }
        return null;
    }

    public ResponseEntity<Response> getFileUrl(String key) {
        return ResponseEntity.ok(new Response("File url", s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build()).toString()));
    }

    public byte[] downloadFile(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request)) {
            return s3Object.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read object bytes from R2", e);
        }
    }


    @Async("imageExecutor")
    public void processAndUpload(Long placeId, List<String> filePaths, Long userId) throws IOException {
        for (String path : filePaths) {
            File originalFile = new File(path);
            String fileUUID = UUID.randomUUID().toString();

            uploadProcessedImage(originalFile, "high", fileUUID,1.0, 0.8f);

            uploadProcessedImage(originalFile, "medium", fileUUID,0.5, 0.7f);
            uploadProcessedImage(originalFile, "low", fileUUID,0.2, 0.5f);

            Files.deleteIfExists(originalFile.toPath());
        }
    }

    private void uploadProcessedImage(File source, String type, String uuid, double scale, float quality) throws IOException {

        String fileName = type + "_" + uuid + ".jpg";

        File tempProcessed = new File(source.getParent(), "temp_" + fileName);

        Thumbnails.of(source).scale(scale).outputFormat("jpg").outputQuality(quality).toFile(tempProcessed);

        String keyForR2 = type + "/" + uuid + ".jpg";
        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(keyForR2).contentType("image/jpeg").acl(ObjectCannedACL.PUBLIC_READ).build(), RequestBody.fromFile(tempProcessed));

        Files.deleteIfExists(tempProcessed.toPath());
    }

}
