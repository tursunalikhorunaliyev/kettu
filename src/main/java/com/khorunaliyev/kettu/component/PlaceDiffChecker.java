package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.dto.request.place.PlaceLocationRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceMetaDataRequest;
import com.khorunaliyev.kettu.dto.request.place.PlacePhotoRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlaceLocation;
import com.khorunaliyev.kettu.entity.place.PlaceMetaData;
import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import com.khorunaliyev.kettu.entity.resources.NearbyThings;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlaceDiffChecker {

    public boolean isPlaceDifferent(Place place, PlaceRequest request) {

        if (request.getName() != null && !place.getName().trim().equalsIgnoreCase(request.getName().trim()))
            return true;
        if (request.getDescription() != null && !place.getDescription().trim().equals(request.getDescription().trim()))
            return true;

        if (request.getPlaceMetaData() != null) {
            PlaceMetaData placeMetaData = place.getMetaData();
            PlaceMetaDataRequest placeMetaDataRequest = request.getPlaceMetaData();

            if (!Objects.equals(placeMetaData.getFeature().getId(), placeMetaDataRequest.getFeatureId())) return true;
            if (!Objects.equals(placeMetaData.getCategory().getId(), placeMetaDataRequest.getCategoryId())) return true;
            return !Objects.equals(placeMetaData.getSubCategory().getId(), placeMetaDataRequest.getSubcategoryId());
        }

        if (request.getPlaceLocation() != null) {
            PlaceLocation placeLocation = place.getLocation();
            PlaceLocationRequest placeLocationRequest = request.getPlaceLocation();

            if (!Objects.equals(placeLocation.getCountry().getId(), placeLocationRequest.getCountryId())) return true;
            if (!Objects.equals(placeLocation.getRegion().getId(), placeLocationRequest.getRegionId())) return true;
            return !Objects.equals(placeLocation.getDistrict().getId(), placeLocationRequest.getDistrictId());
        }

        if(request.getPlacePhotos()!=null){
            List<String> placePhotos = place.getPhotos().stream().map(PlacePhoto::getImage).toList();
            List<String> placePhotoRequests = request.getPlacePhotos().stream().map(PlacePhotoRequest::getImageName).toList();
            return !placePhotos.equals(placePhotoRequests);
        }

        if(request.getNearbyThings()!=null){
            Set<Long> placeNearbyThings = place.getNearbyThings().stream().map(NearbyThings::getId).collect(Collectors.toSet());
            return !(new HashSet<>(request.getNearbyThings()).equals(placeNearbyThings));
        }

        return false;
    }
}
