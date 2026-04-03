package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.dto.reponse.place.PhotoTask;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import com.khorunaliyev.kettu.repository.place.PlacePhotoRepository;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.place.UserActiveUploadsRepository;
import com.khorunaliyev.kettu.services.storage.R2Service;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacePhotoService {
    private final PlacePhotoRepository placePhotoRepository;
    private final PlaceRepository placeRepository;

    private final UserActiveUploadsRepository userActiveUploadsRepository;
    private final R2Service r2Service;
    private final EntityManager entityManager;


    @Async("imageExecutor")
    public void processAndUpload(Long placeId, String mainPhotoPath, List<String> additionalPhotoPaths, Long userId) {

        try {
            Stream<PhotoTask> mainStream = Stream.of(new PhotoTask(mainPhotoPath, true));
            Stream<PhotoTask> additionalStream = (additionalPhotoPaths != null)
                    ? additionalPhotoPaths.stream().map(path -> new PhotoTask(path, false))
                    : Stream.empty();

            Stream.concat(mainStream, additionalStream).filter(photoTask -> photoTask.path() != null).forEach(photoTask -> {
                try {
                    processSingleImage(placeId, photoTask);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            updatePlaceStatus(placeId, PlaceStatus.IN_MODERATION);
        } catch (Exception e) {
            System.out.println(e);
            updatePlaceStatus(placeId, PlaceStatus.FAILED);
            cleanupFailedUpload(placeId);
        } finally {
            userActiveUploadsRepository.deleteByPlace_IdAndUser_Id(placeId, userId);
        }
    }

    private void processSingleImage(Long placeId, PhotoTask photoTask) throws IOException {
        File orginalFile = new File(photoTask.path());
        String uuid = UUID.randomUUID().toString();

        try {
            compressAndUpload(orginalFile, "high", uuid, 1.0, 0.8f);
            compressAndUpload(orginalFile, "medium", uuid, 0.5, 0.7f);
            compressAndUpload(orginalFile, "low", uuid, 0.2, 0.5f);
            savePhotoToDB(placeId, uuid, photoTask.isMain());
        } finally {
            try {
                Files.deleteIfExists(orginalFile.toPath());
            } catch (Exception e) {
                log.warn("Could not delete temp file: {}", orginalFile.getPath());
            }
        }
    }

    private void compressAndUpload(File source, String type, String uuid, double scale, float quality) throws IOException {

        String fileName = type + "_" + uuid + ".jpg";

        File tempProcessed = new File(source.getParent(), "temp_" + fileName);
        try {
            Thumbnails.of(source)
                    .scale(scale)
                    .outputFormat("jpg")
                    .outputQuality(quality)
                    .toFile(tempProcessed);

            String keyForR2 = type + "/" + uuid + ".jpg";

            r2Service.upload(tempProcessed, keyForR2);

        } finally {
            Files.deleteIfExists(tempProcessed.toPath());
        }
    }

    private void updatePlaceStatus(Long placeId, PlaceStatus placeStatus) {
        placeRepository.updateStatus(placeId, placeStatus);
    }

    private void savePhotoToDB(Long placeId, String uuid, boolean isMain) {
        PlacePhoto photo = new PlacePhoto();
        photo.setPlace(entityManager.getReference(Place.class, placeId));
        photo.setImage(uuid);
        photo.setMain(isMain);
        placePhotoRepository.save(photo);
    }

    private void cleanupFailedUpload(Long placeId) {
        List<PlacePhoto> placePhotos = placePhotoRepository.findByPlace_Id(placeId);
        for (PlacePhoto photo : placePhotos) {
            String uuid = photo.getImage();

            r2Service.deleteFile("high/" + uuid + ".jpg");
            r2Service.deleteFile("medium/" + uuid + ".jpg");
            r2Service.deleteFile("low/" + uuid + ".jpg");
        }
        placePhotoRepository.deleteByPlace_Id(placeId);

    }
}
