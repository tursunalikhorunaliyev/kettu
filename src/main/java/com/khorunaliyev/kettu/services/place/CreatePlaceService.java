package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.place.PlaceLocationRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceMetaDataRequest;
import com.khorunaliyev.kettu.dto.request.place.PlacePhotoRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlaceLocation;
import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePlaceService {

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    @CacheEvict(value = "places", allEntries = true)
    public ResponseEntity<Response> createPlace(PlaceRequest request) {

        String name = request.getName();
        String description = request.getDescription();
        PlaceMetaDataRequest placeMetaDataRequest = request.getPlaceMetaData();
        List<PlacePhotoRequest> placePhotos = request.getPlacePhotos();
        PlaceLocationRequest location = request.getPlaceLocation();
        List<Long> nearByThings = request.getNearbyThings();

        Place place = new Place();

        //PlaceLocation creation
        PlaceLocation placeLocation = new PlaceLocation();

//        placeLocation.setCountry(countryRepository.findById(location.getCountryId()).orElseThrow(() -> new RequestRejectedException("Country not found")));
//        placeLocation.setRegion(regionRepository.findById(location.getRegionId()).orElseThrow(() -> new ResourceNotFoundException("Region not found")));
//        placeLocation.setDistrict(districtRepository.findById(location.getDistrictId()).orElseThrow(() -> new ResourceNotFoundException("District not found")));
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
        Point point = geometryFactory.createPoint(new Coordinate(location.getLat_(), location.getLong_()));
        placeLocation.setPoint(point);
        placeLocation.setPlace(place);


        //PlacePhoto entities creation
        List<PlacePhoto> placePhotoEntities = placePhotos.stream().map(placePhotoRequest -> {
            PlacePhoto placePhoto = new PlacePhoto();
            placePhoto.setImage(placePhotoRequest.getImageName());
            placePhoto.setMain(placePhotoRequest.getIsMain());
            placePhoto.setPlace(place);
            return placePhoto;
        }).toList();


        //Place NearbyThings finding

        //PlaceMetaData creating


        place.setName(name.trim());
        place.setDescription(description.trim());
        place.setLocation(placeLocation);
        place.setPhotos(placePhotoEntities);

        //place.setMetaData(placeMetaData);

        placeRepository.save(place);

        return ResponseEntity.ok(new Response("Place successfully created", null));

    }
}
