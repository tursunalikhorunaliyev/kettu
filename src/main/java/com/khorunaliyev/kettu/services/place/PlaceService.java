package com.khorunaliyev.kettu.services.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.khorunaliyev.kettu.component.PlaceDiffChecker;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.*;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameDTO;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import com.khorunaliyev.kettu.dto.reponse.resource.NearbyThingsDTO;
import com.khorunaliyev.kettu.dto.request.place.*;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.*;
import com.khorunaliyev.kettu.entity.resources.*;
import com.khorunaliyev.kettu.repository.place.PlaceHistoryRepository;
import com.khorunaliyev.kettu.repository.place.PlacePhotoRepository;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
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
    private final PlacePhotoRepository placePhotoRepository;


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

    public ResponseEntity<Response> changePlaceStatus(Long placeID, PlaceUpdateStatusRequest request) {

        Place place = placeRepository.findById(placeID).orElseThrow(() -> new ResourceNotFoundException("Place not found"));

        String statusString = request.getStatus().trim();
        String changeReason = request.getChangeReason();

        PlaceStatus status;
        try {
            status = PlaceStatus.valueOf(statusString.toUpperCase());
        }
        catch (RuntimeException e){
            throw new ResourceNotFoundException("Status not found");
        }

        if(status==place.getStatus()){
            return new ResponseEntity<>(new Response("Nothing changed", null), HttpStatus.NO_CONTENT);
        }


        String snapshot;
        try {
            PlaceHistoryDTO dto = placeHistoryDTO(place); // use your manual or MapStruct mapper
            snapshot = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to snapshot place", e);
        }


        if (status == PlaceStatus.ACTIVE) {
            Category category = place.getMetaData().getCategory();
            int categoryActiveItemCount = category.getActiveItemCount() + 1;
            if (categoryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            category.setActiveItemCount(categoryActiveItemCount);
            categoryRepository.save(category);

            SubCategory subCategory = place.getMetaData().getSubCategory();
            int subCategoryActiveItemCount = subCategory.getActiveItemCount() + 1;
            if (subCategoryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            subCategory.setActiveItemCount(subCategoryActiveItemCount);
            subCategoryRepository.save(subCategory);

            Country country = place.getLocation().getCountry();
            int countryActiveItemCount = country.getActiveItemCount() + 1;
            if (countryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            country.setActiveItemCount(countryActiveItemCount);
            countryRepository.save(country);

            Region region = place.getLocation().getRegion();
            int regionActiveItemCount = region.getActiveItemCount() + 1;
            if (regionActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            region.setActiveItemCount(regionActiveItemCount);
            regionRepository.save(region);

            District district = place.getLocation().getDistrict();
            int districtActiveItemCount = district.getActiveItemCount() + 1;
            if (districtActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            district.setActiveItemCount(districtActiveItemCount);
            districtRepository.save(district);
        }

        if (place.getStatus() == PlaceStatus.ACTIVE && (status != PlaceStatus.ACTIVE)) {
            Category category = place.getMetaData().getCategory();
            int categoryActiveItemCount = category.getActiveItemCount() - 1;
            if (categoryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            category.setActiveItemCount(categoryActiveItemCount);
            categoryRepository.save(category);

            SubCategory subCategory = place.getMetaData().getSubCategory();
            int subCategoryActiveItemCount = subCategory.getActiveItemCount() - 1;
            if (subCategoryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            subCategory.setActiveItemCount(subCategoryActiveItemCount);
            subCategoryRepository.save(subCategory);

            Country country = place.getLocation().getCountry();
            int countryActiveItemCount = country.getActiveItemCount() - 1;
            if (countryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            country.setActiveItemCount(countryActiveItemCount);
            countryRepository.save(country);

            Region region = place.getLocation().getRegion();
            int regionActiveItemCount = region.getActiveItemCount() - 1;
            if (regionActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            region.setActiveItemCount(regionActiveItemCount);
            regionRepository.save(region);

            District district = place.getLocation().getDistrict();
            int districtActiveItemCount = district.getActiveItemCount() - 1;
            if (districtActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            district.setActiveItemCount(districtActiveItemCount);
            districtRepository.save(district);
        }


        place.setStatus(status);
        placeRepository.save(place);

        try {
            PlaceHistory placeHistory = new PlaceHistory();
            placeHistory.setPlace(place);
            placeHistory.setPlaceSnapshotJson(snapshot);
            if (changeReason != null) placeHistory.setChangeReason(changeReason.trim());
            placeHistoryRepository.save(placeHistory);
        } catch (Exception e) {
            throw new RequestRejectedException("Request rejected");
        }

        return ResponseEntity.ok(new Response("Place updated", null));
    }

    @CacheEvict(value = "places", allEntries = true)
    public ResponseEntity<Response> update(Long placeId, PlaceUpdateRequest request) {
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new ResourceNotFoundException("Place not found"));

        if (place.getStatus() == PlaceStatus.MODERATION) {
            return new ResponseEntity<>(new Response("You can't update while moderation status", null), HttpStatus.BAD_REQUEST);
        }

        if (!placeDiffChecker.isPlaceDifferent(place, request)) {
            return new ResponseEntity<>(new Response("No difference", null), HttpStatus.NO_CONTENT);
        }

        String snapshot;
        try {
            PlaceHistoryDTO dto = placeHistoryDTO(place); // use your manual or MapStruct mapper
            snapshot = objectMapper.writeValueAsString(dto);
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
            List<PlacePhoto> newPhotos = request.getPlacePhotos().stream().map(ph -> {
                PlacePhoto photo = new PlacePhoto();
                photo.setImage(ph.getImageName());
                photo.setMain(ph.getIsMain());
                photo.setPlace(place);
                return photo;
            }).toList();

            place.getPhotos().clear();
            place.getPhotos().addAll(newPhotos);
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
            if (categoryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            category.setActiveItemCount(categoryActiveItemCount);
            categoryRepository.save(category);

            SubCategory subCategory = place.getMetaData().getSubCategory();
            int subCategoryActiveItemCount = subCategory.getActiveItemCount() - 1;
            if (subCategoryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            subCategory.setActiveItemCount(subCategoryActiveItemCount);
            subCategoryRepository.save(subCategory);

            Country country = place.getLocation().getCountry();
            int countryActiveItemCount = country.getActiveItemCount() - 1;
            if (countryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            country.setActiveItemCount(countryActiveItemCount);
            countryRepository.save(country);

            Region region = place.getLocation().getRegion();
            int regionActiveItemCount = region.getActiveItemCount() - 1;
            if (regionActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            region.setActiveItemCount(regionActiveItemCount);
            regionRepository.save(region);

            District district = place.getLocation().getDistrict();
            int districtActiveItemCount = district.getActiveItemCount() - 1;
            if (districtActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            district.setActiveItemCount(districtActiveItemCount);
            districtRepository.save(district);

        }
        placeRepository.save(place);
        placeHistoryRepository.save(placeHistory);

        return ResponseEntity.ok(new Response("Place updated", null));

    }

    @Cacheable("places")
    public ResponseEntity<List<PlaceDTO>> getAllPlaces() {

        List<PlaceDTO> places = placeRepository.findAllBy().stream().map(this::toDto).toList();

        return ResponseEntity.ok(places);
    }

    private PlaceDTO toDto(PlaceInfo p) {
        return new PlaceDTO(p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStatus(),
                new PlacePhotoDTO(p.getPhotos().stream()
                        .filter(PlaceInfo.PlacePhotoInfo::isIsMain)
                        .map(PlaceInfo.PlacePhotoInfo::getImage)
                        .findFirst().orElse(null), p.getPhotos().stream().filter(placePhotoInfo -> !placePhotoInfo.isIsMain()).map(PlaceInfo.PlacePhotoInfo::getImage).toList()),
                new PlaceMetaDataDTO(p.getMetaData().getId(),
                        new IDNameDTO(p.getMetaData().getFeature().getId(), p.getMetaData().getFeature().getName()),
                        new IDNameItemCountDTO(p.getMetaData().getCategory().getId(), p.getMetaData().getCategory().getName(), p.getMetaData().getCategory().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getMetaData().getSubCategory().getId(), p.getMetaData().getSubCategory().getName(), p.getMetaData().getSubCategory().getActiveItemCount())),
                new PlaceLocationDTO(p.getLocation().getId(),
                        new IDNameItemCountDTO(p.getLocation().getCountry().getId(), p.getLocation().getCountry().getName(), p.getLocation().getCountry().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getLocation().getRegion().getId(), p.getLocation().getRegion().getName(), p.getLocation().getRegion().getActiveItemCount()),
                        new IDNameDTO(p.getLocation().getDistrict().getId(), p.getLocation().getDistrict().getName())),
                p.getNearbyThings().stream().map(ns -> new NearbyThingsDTO(ns.getId(), ns.getName(), "https://storage.thekettu.com/"+ns.getIcon())).collect(Collectors.toSet()),
                p.getVisitedUsers().stream().map(vu -> new UserDTO(vu.getId(), vu.getName(), vu.getEmail())).collect(Collectors.toSet()),
                p.getLikedUsers().stream().map(lu -> new UserDTO(lu.getId(), lu.getName(), lu.getEmail())).collect(Collectors.toSet()),
                p.getLikesCount(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private PlaceHistoryDTO placeHistoryDTO(Place p) {
        return new PlaceHistoryDTO(p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStatus(),
                new PlacePhotoDTO(p.getPhotos().stream()
                        .filter(PlacePhoto::isMain)
                        .map(PlacePhoto::getImage)
                        .findFirst().orElse(null), p.getPhotos().stream().filter(placePhotoInfo -> !placePhotoInfo.isMain()).map(PlacePhoto::getImage).toList()),
                new PlaceMetaDataDTO(p.getMetaData().getId(),
                        new IDNameDTO(p.getMetaData().getFeature().getId(), p.getMetaData().getFeature().getName()),
                        new IDNameItemCountDTO(p.getMetaData().getCategory().getId(), p.getMetaData().getCategory().getName(), p.getMetaData().getCategory().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getMetaData().getSubCategory().getId(), p.getMetaData().getSubCategory().getName(), p.getMetaData().getSubCategory().getActiveItemCount())),
                new PlaceLocationDTO(p.getLocation().getId(),
                        new IDNameItemCountDTO(p.getLocation().getCountry().getId(), p.getLocation().getCountry().getName(), p.getLocation().getCountry().getActiveItemCount()),
                        new IDNameItemCountDTO(p.getLocation().getRegion().getId(), p.getLocation().getRegion().getName(), p.getLocation().getRegion().getActiveItemCount()),
                        new IDNameDTO(p.getLocation().getDistrict().getId(), p.getLocation().getDistrict().getName())),
                p.getNearbyThings().stream().map(ns -> new NearbyThingsDTO(ns.getId(), ns.getName(), ns.getIcon())).collect(Collectors.toSet()),
                p.getVisitedUsers().stream().map(vu -> new UserDTO(vu.getId(), vu.getName(), vu.getEmail())).collect(Collectors.toSet()),
                p.getLikedUsers().stream().map(lu -> new UserDTO(lu.getId(), lu.getName(), lu.getEmail())).collect(Collectors.toSet()),
                p.getLikesCount(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                new UserDTO(p.getCreatedBy().getId(), p.getCreatedBy().getName(), p.getCreatedBy().getEmail()),
                new UserDTO(p.getUpdatedBy().getId(), p.getUpdatedBy().getName(), p.getUpdatedBy().getEmail())
        );
    }
}
