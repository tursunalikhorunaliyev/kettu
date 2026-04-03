package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.component.UserContext;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.PhotoData;
import com.khorunaliyev.kettu.dto.reponse.place.UploadingPlaceData;
import com.khorunaliyev.kettu.repository.place.UserActiveUploadsRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final UserContext userContext;
    private final UserActiveUploadsRepository activeUploadsRepository;

    @Cacheable("places")
    public ResponseEntity<Response> getAllPlaces(Long categoryId, Long districtId, Long regionId, Long countryId, Pageable pageable) {

        if ((countryId != null || regionId != null || districtId != null) && (categoryId == null)) {
            if (countryId != null && countryId < 1) {
                throw new ResourceNotFoundException("Resource not found. Bad element");
            } else if (regionId != null && regionId < 1) {
                throw new ResourceNotFoundException("Resource not found. Bad element");
            } else if (districtId != null && districtId < 1) {
                throw new ResourceNotFoundException("Resource not found. Bad element");
            }
            //find with place location
        } else {

            //find with place itself
        }
        return ResponseEntity.ok(new Response("ok", "ok"));

    }

    public ResponseEntity<Response> checkUploadingPlaces() {
        Long userID = userContext.getUserId();

        List<Tuple> tuple = activeUploadsRepository.getProcessedPlaces(userID);
        List<UploadingPlaceData> uploadingPlaceData = tuple.stream().map(tpl -> {
            String mainPhotoName = tpl.get("main_photo", String.class);
            return
                    new UploadingPlaceData(
                            tpl.get("place_id", Long.class),
                            tpl.get("name", String.class),
                            tpl.get("description", String.class),
                            tpl.get("photo_count", Integer.class),
                            tpl.get("uploaded_photo_count", Long.class),
                            mainPhotoName != null ? new PhotoData("https://storage.thekettu.com/high/" + mainPhotoName + ".jpg",
                                    "https://storage.thekettu.com/medium/" + mainPhotoName + ".jpg",
                                    "https://storage.thekettu.com/low/" + mainPhotoName + ".jpg") : null);
        }).toList();


        return ResponseEntity.ok(new Response("Success", uploadingPlaceData));
    }


//    public ResponseEntity<List<PlaceDTO>> getNearbyPlaces(LatLongRequest latLongRequest){
//
//    }


}
