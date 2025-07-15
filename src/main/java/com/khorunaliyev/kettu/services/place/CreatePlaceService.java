package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.place.PlaceLocationRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceMetaDataRequest;
import com.khorunaliyev.kettu.dto.request.place.PlacePhotoRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlaceLocation;
import com.khorunaliyev.kettu.entity.place.PlaceMetaData;
import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import com.khorunaliyev.kettu.entity.resources.NearbyThings;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreatePlaceService {

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NearbyThingsRepository nearbyThingsRepository;
    private final FeatureRepository featureRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
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
        placeLocation.setCountry(countryRepository.findById(location.getCountryId()).orElseThrow(() -> new RequestRejectedException("Country not found")));
        placeLocation.setRegion(regionRepository.findById(location.getRegionId()).orElseThrow(() -> new ResourceNotFoundException("Region not found")));
        placeLocation.setDistrict(districtRepository.findById(location.getDistrictId()).orElseThrow(() -> new ResourceNotFoundException("District not found")));
        placeLocation.setLat_(location.getLat_());
        placeLocation.setLong_(location.getLong_());
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
        Set<NearbyThings> placeNearbyThings = new HashSet<>(nearbyThingsRepository.findAllByIds(nearByThings));
        if (placeNearbyThings.size() != nearByThings.size())
            throw new ResourceNotFoundException("Nearby things not found or not found fully");


        //PlaceMetaData creating
        PlaceMetaData placeMetaData = new PlaceMetaData();
        placeMetaData.setFeature(featureRepository.findById(placeMetaDataRequest.getFeatureId()).orElseThrow(() -> new ResourceNotFoundException("Feature not found")));
        placeMetaData.setCategory(categoryRepository.findById(placeMetaDataRequest.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        placeMetaData.setSubCategory(subCategoryRepository.findById(placeMetaDataRequest.getSubcategoryId()).orElseThrow(() -> new ResourceNotFoundException("Subcategory not found")));
        placeMetaData.setPlace(place);


        place.setName(name.trim());
        place.setDescription(description.trim());
        place.setLocation(placeLocation);
        place.setPhotos(placePhotoEntities);
        place.setNearbyThings(placeNearbyThings);
        place.setMetaData(placeMetaData);

        placeRepository.save(place);

        return ResponseEntity.ok(new Response("Place successfully created", null));

    }
}
