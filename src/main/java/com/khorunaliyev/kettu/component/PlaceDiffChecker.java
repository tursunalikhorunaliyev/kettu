package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.dto.request.place.*;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.entity.place.PlaceLocation;
import com.khorunaliyev.kettu.entity.place.PlaceMetaData;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlaceDiffChecker {

    public boolean isPlaceDifferent(Place place, PlaceUpdateRequest request) {

        if (request.getName() != null && !place.getName().trim().equalsIgnoreCase(request.getName().trim()))
            return true;
        if (request.getDescription() != null && !place.getDescription().trim().equals(request.getDescription().trim()))
            return true;

        if (request.getPlaceMetaData() != null) {
            PlaceMetaData placeMetaData = place.getMetaData();
            PlaceMetaDataRequest placeMetaDataRequest = request.getPlaceMetaData();

            if (!Objects.equals(placeMetaData.getCategory().getId(), placeMetaDataRequest.getCategoryId())) return true;

        }


        if(request.getPlacePhotos()!=null){
            List<String> placePhotos = place.getPhotos().stream().map(placePhoto -> placePhoto.getImage()+placePhoto.isMain()).toList();
            List<String> placePhotoRequests = request.getPlacePhotos().stream().map(placePhotoRequest -> placePhotoRequest.getImageName()+placePhotoRequest.getIsMain()).toList();
            System.out.println(placePhotos);
            System.out.println(request.getPlacePhotos().get(0).getIsMain());
            return !placePhotos.equals(placePhotoRequests);
        }


        return false;
    }

}
