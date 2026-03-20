package com.khorunaliyev.kettu.services.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khorunaliyev.kettu.component.PlaceDiffChecker;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.component.PlaceMappers;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.*;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateRequest;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.*;
import com.khorunaliyev.kettu.entity.resources.*;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdatePlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceDiffChecker placeDiffChecker;
    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;
    private final PlaceMappers placeMappers;

    @CacheEvict(value = "places", allEntries = true)
    public ResponseEntity<Response> update(Long placeId, PlaceUpdateRequest request) {
        Place place = placeRepository.findById(placeId).orElseThrow(() -> new ResourceNotFoundException("Place not found"));

        if (place.getStatus() == PlaceStatus.IN_MODERATION) {
            return new ResponseEntity<>(new Response("You can't update while moderation status", null), HttpStatus.BAD_REQUEST);
        }

        if (!placeDiffChecker.isPlaceDifferent(place, request)) {
            return new ResponseEntity<>(new Response("No difference", null), HttpStatus.NO_CONTENT);
        }


        if (request.getName() != null) place.setName(request.getName().trim());
        if (request.getDescription() != null) place.setDescription(request.getDescription().trim());


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

            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            Point point = geometryFactory.createPoint(new Coordinate(request.getPlaceLocation().getLat_(), request.getPlaceLocation().getLong_()));
            loc.setPoint(point);
            place.setLocation(loc);
        }

        if (request.getPlaceMetaData() != null) {
            PlaceMetaData placeMetaData = new PlaceMetaData();
            placeMetaData.setCategory(categoryRepository.findById(request.getPlaceMetaData().getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found")));
            place.setMetaData(placeMetaData);
        }

        if (request.getPlaceLocation() != null || request.getPlacePhotos() != null || request.getName() != null || request.getDescription() != null) {

            place.setStatus(PlaceStatus.IN_MODERATION);

            Category category = place.getMetaData().getCategory();
            int categoryActiveItemCount = category.getActiveItemCount() - 1;
            if (categoryActiveItemCount < 0)
                return new ResponseEntity<>(new Response("Count is negative. Problem has occurred with count", null), HttpStatus.CONFLICT);
            category.setActiveItemCount(categoryActiveItemCount);
            categoryRepository.save(category);


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

        return ResponseEntity.ok(new Response("Place updated", null));

    }


}
