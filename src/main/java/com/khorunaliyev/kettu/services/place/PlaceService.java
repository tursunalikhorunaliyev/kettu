package com.khorunaliyev.kettu.services.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khorunaliyev.kettu.component.PlaceDiffChecker;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.place.PlaceLocationRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceMetaDataRequest;
import com.khorunaliyev.kettu.dto.request.place.PlacePhotoRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.*;
import com.khorunaliyev.kettu.entity.resources.*;
import com.khorunaliyev.kettu.repository.place.PlaceHistoryRepository;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaceService {

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final NearbyThingsRepository nearbyThingsRepository;
    private final FeatureRepository featureRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final PlaceRepository placeRepository;

    private final ObjectMapper objectMapper;
    private final PlaceHistoryRepository placeHistoryRepository;
    private final PlaceDiffChecker placeDiffChecker;


    @Transactional
    public ResponseEntity<Response> createPlace(String name, String description, PlaceMetaDataRequest placeMetaDataRequest, List<PlacePhotoRequest> placePhotos, PlaceLocationRequest location, List<Long> nearByThings) {
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
            placePhoto.setMain(placePhotoRequest.isMain());
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

    public ResponseEntity<Response> changePlaceStatus(Long placeID, PlaceStatus status, String changeReason) {
        Place place = placeRepository.findById(placeID).orElseThrow(() -> new ResourceNotFoundException("Place not found"));

        String snapshotJson;
        try {
            snapshotJson = objectMapper.writeValueAsString(place);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create snapshot", e);
        }


        if (status == PlaceStatus.ACTIVE) {
            Category category = place.getMetaData().getCategory();
            int categoryActiveItemCount = category.getActiveItemCount() + 1;
            if(categoryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            category.setActiveItemCount(categoryActiveItemCount);
            categoryRepository.save(category);

            SubCategory subCategory = place.getMetaData().getSubCategory();
            int subCategoryActiveItemCount = subCategory.getActiveItemCount() + 1;
            if(subCategoryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            subCategory.setActiveItemCount(subCategoryActiveItemCount);
            subCategoryRepository.save(subCategory);

            Country country = place.getLocation().getCountry();
            int countryActiveItemCount = country.getActiveItemCount() + 1;
            if(countryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            country.setActiveItemCount(countryActiveItemCount);
            countryRepository.save(country);

            Region region = place.getLocation().getRegion();
            int regionActiveItemCount = region.getActiveItemCount() + 1;
            if(regionActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            region.setActiveItemCount(regionActiveItemCount);
            regionRepository.save(region);

            District district = place.getLocation().getDistrict();
            int districtActiveItemCount = subCategory.getActiveItemCount() + 1;
            if(districtActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            district.setActiveItemCount(districtActiveItemCount);
            districtRepository.save(district);
        }

        if (place.getStatus() == PlaceStatus.ACTIVE && (status != PlaceStatus.ACTIVE)) {
            Category category = place.getMetaData().getCategory();
            int categoryActiveItemCount = category.getActiveItemCount() - 1;
            if(categoryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            category.setActiveItemCount(categoryActiveItemCount);
            categoryRepository.save(category);

            SubCategory subCategory = place.getMetaData().getSubCategory();
            int subCategoryActiveItemCount = subCategory.getActiveItemCount() - 1;
            if(subCategoryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            subCategory.setActiveItemCount(subCategoryActiveItemCount);
            subCategoryRepository.save(subCategory);

            Country country = place.getLocation().getCountry();
            int countryActiveItemCount = country.getActiveItemCount() - 1;
            if(countryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            country.setActiveItemCount(countryActiveItemCount);
            countryRepository.save(country);

            Region region = place.getLocation().getRegion();
            int regionActiveItemCount = region.getActiveItemCount() - 1;
            if(regionActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            region.setActiveItemCount(regionActiveItemCount);
            regionRepository.save(region);

            District district = place.getLocation().getDistrict();
            int districtActiveItemCount = district.getActiveItemCount() - 1;
            if(districtActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            district.setActiveItemCount(districtActiveItemCount);
            districtRepository.save(district);
        }

        try {
            PlaceHistory placeHistory = new PlaceHistory();
            placeHistory.setPlace(place);
            placeHistory.setPlaceSnapshotJson(snapshotJson);
            if (changeReason != null) placeHistory.setChangeReason(changeReason);
            placeHistoryRepository.save(placeHistory);
        } catch (Exception e) {
            throw new RequestRejectedException("Request rejected");
        }


        place.setStatus(status);
        placeRepository.save(place);


        return ResponseEntity.ok(new Response("Place updated", null));
    }


    public ResponseEntity<Response> update(Long placeId, PlaceRequest request) {
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new ResourceNotFoundException("Place not found"));

        if (place.getStatus() == PlaceStatus.MODERATION) {
            return new ResponseEntity<>(new Response("You can't update while moderation status", null), HttpStatus.BAD_REQUEST);
        }

        if (!placeDiffChecker.isPlaceDifferent(place, request)) {
            return new ResponseEntity<>(new Response("No difference", null), HttpStatus.NO_CONTENT);
        }

        String snapshot;
        try {
            snapshot = objectMapper.writeValueAsString(place);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to snapshot place", e);
        }


        PlaceHistory placeHistory = new PlaceHistory();
        placeHistory.setPlace(place);
        placeHistory.setPlaceSnapshotJson(snapshot);

        if (request.getName() != null) place.setName(request.getName().trim());
        if (request.getDescription() != null) place.setDescription(request.getDescription().trim());

        if (request.getNearbyThings() != null) {
            Set<NearbyThings> things = new HashSet<>(nearbyThingsRepository.findAllByIds(request.getNearbyThings()));
            if (things.size() != request.getNearbyThings().size())
                throw new ResourceNotFoundException("Some nearby things not found");
            place.setNearbyThings(things);
        }

        if (request.getPlacePhotos() != null) {
            List<PlacePhoto> newPhotos = request.getPlacePhotos().stream().map(p -> {
                PlacePhoto photo = new PlacePhoto();
                photo.setImage(p.getImageName());
                photo.setMain(p.isMain());
                photo.setPlace(place);
                return photo;
            }).toList();
            place.setPhotos(newPhotos);
        }


        if (request.getPlaceLocation() != null) {
            PlaceLocation loc = new PlaceLocation();
            loc.setPlace(place);
            loc.setCountry(countryRepository.findById(request.getPlaceLocation().getCountryId()).orElseThrow(() -> new ResourceNotFoundException("Country not found")));
            loc.setRegion(regionRepository.findById(request.getPlaceLocation().getRegionId()).orElseThrow(() -> new ResourceNotFoundException("Region not found")));
            loc.setDistrict(districtRepository.findById(request.getPlaceLocation().getDistrictId()).orElseThrow(() -> new ResourceNotFoundException("District not found")));
            loc.setLat_(request.getPlaceLocation().getLat_());
            loc.setLong_(request.getPlaceLocation().getLong_());
            place.setLocation(loc);
        }

        if (request.getPlaceMetaData() != null) {
            PlaceMetaData placeMetaData = new PlaceMetaData();
            placeMetaData.setFeature(featureRepository.findById(request.getPlaceMetaData().getFeatureId()).orElseThrow(() -> new ResourceNotFoundException("Feature not found")));
            placeMetaData.setCategory(categoryRepository.findById(request.getPlaceMetaData().getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found")));
            placeMetaData.setSubCategory(subCategoryRepository.findById(request.getPlaceMetaData().getSubcategoryId()).orElseThrow(() -> new ResourceNotFoundException("Subcategory not found")));
            place.setMetaData(placeMetaData);
        }

        if (request.getPlaceLocation() != null || request.getPlacePhotos() != null || request.getName() != null || request.getDescription() != null) {

            place.setStatus(PlaceStatus.MODERATION);

            Category category = place.getMetaData().getCategory();
            int categoryActiveItemCount = category.getActiveItemCount() - 1;
            if(categoryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            category.setActiveItemCount(categoryActiveItemCount);
            categoryRepository.save(category);

            SubCategory subCategory = place.getMetaData().getSubCategory();
            int subCategoryActiveItemCount = subCategory.getActiveItemCount() - 1;
            if(subCategoryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            subCategory.setActiveItemCount(subCategoryActiveItemCount);
            subCategoryRepository.save(subCategory);

            Country country = place.getLocation().getCountry();
            int countryActiveItemCount = country.getActiveItemCount() - 1;
            if(countryActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            country.setActiveItemCount(countryActiveItemCount);
            countryRepository.save(country);

            Region region = place.getLocation().getRegion();
            int regionActiveItemCount = region.getActiveItemCount() - 1;
            if(regionActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            region.setActiveItemCount(regionActiveItemCount);
            regionRepository.save(region);

            District district = place.getLocation().getDistrict();
            int districtActiveItemCount = district.getActiveItemCount() - 1;
            if(districtActiveItemCount<0) return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            district.setActiveItemCount(districtActiveItemCount);
            districtRepository.save(district);

        }
        placeRepository.save(place);
        placeHistoryRepository.save(placeHistory);

        return ResponseEntity.ok(new Response("Place updated", null));

    }
}
