package com.khorunaliyev.kettu.services.storage;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.repository.place.PlacePhotoRepository;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.place.UserActiveUploadsRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class R2ServiceTest {

    @Mock private PlacePhotoRepository placePhotoRepository;
    @Mock private S3Client s3Client;
    @Mock private UserActiveUploadsRepository activeUploadsRepository;
    @Mock private PlaceRepository placeRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks private R2Service r2Service;

    @BeforeEach
    void setBucket() {
        ReflectionTestUtils.setField(r2Service, "bucket", "test-bucket");
    }

    @Test
    void deleteFilesRemovesEveryKey() {
        ResponseEntity<Response> result = r2Service.deleteFiles(List.of("a.jpg", "b.jpg", "c.jpg"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(s3Client, times(3)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFilesOnEmptyListIsANoOpSuccess() {
        ResponseEntity<Response> result = r2Service.deleteFiles(List.of());

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFilesOnNullListIsANoOpSuccess() {
        ResponseEntity<Response> result = r2Service.deleteFiles(null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(s3Client, never()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void deleteFilesReturnsConflictWhenS3Fails() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("boom").build());

        ResponseEntity<Response> result = r2Service.deleteFiles(List.of("a.jpg"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void uploadMultipleSkipsNonImageFiles() {
        MultipartFile image = new MockMultipartFile("f1", "pic.jpg", "image/jpeg", new byte[]{1, 2, 3});
        MultipartFile notImage = new MockMultipartFile("f2", "doc.txt", "text/plain", new byte[]{4, 5});

        ResponseEntity<Response> result = r2Service.uploadMultiple(List.of(image, notImage));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        @SuppressWarnings("unchecked")
        List<String> keys = (List<String>) result.getBody().data();
        assertThat(keys).hasSize(1);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(software.amazon.awssdk.core.sync.RequestBody.class));
    }
}
