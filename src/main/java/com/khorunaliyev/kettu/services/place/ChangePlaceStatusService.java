package com.khorunaliyev.kettu.services.place;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.component.PlaceMappers;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.PlaceHistoryDTO;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateStatusRequest;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlaceHistory;
import com.khorunaliyev.kettu.entity.resources.*;
import com.khorunaliyev.kettu.repository.place.PlaceHistoryRepository;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangePlaceStatusService {

    private final PlaceRepository placeRepository;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final PlaceHistoryRepository placeHistoryRepository;
    private final PlaceMappers placeMappers;

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
            PlaceHistoryDTO dto = placeMappers.placeHistoryDTO(place); // use your manual or MapStruct mapper
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
}
