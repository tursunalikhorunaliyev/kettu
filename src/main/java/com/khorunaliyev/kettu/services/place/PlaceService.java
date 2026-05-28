package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.component.UserContext;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.projection.place.PlaceInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.PhotoData;
import com.khorunaliyev.kettu.dto.reponse.place.UploadingPlaceData;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.place.UserActiveUploadsRepository;
import com.khorunaliyev.kettu.util.query.PlaceSpecification;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final UserContext userContext;
    private final UserActiveUploadsRepository activeUploadsRepository;
    private final PlaceRepository placeRepository;
    private final ProjectionFactory projectionFactory;

    @Cacheable(value = "places")
    public ResponseEntity<Response> getAllPlaces(PlaceStatus status,
                                                 String name,
                                                 Integer categoryId,
                                                 List<Integer> tagIds,
                                                 Integer regionId,
                                                 Integer districtId) {

        Specification<Place> spec = PlaceSpecification.filterBy(status,name,categoryId,tagIds,regionId,districtId);
        return ResponseEntity.ok(new Response("ok", placeRepository.findAll(spec).stream().map(place -> projectionFactory.createProjection(PlaceInfo.class,place)).toList()));

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
