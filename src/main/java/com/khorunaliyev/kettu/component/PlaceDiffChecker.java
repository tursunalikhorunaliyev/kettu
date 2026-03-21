package com.khorunaliyev.kettu.component;

import com.khorunaliyev.kettu.dto.request.place.*;
import com.khorunaliyev.kettu.entity.place.Place;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PlaceDiffChecker {

    public boolean isPlaceDifferent(Place place, PlaceUpdateRequest request) {

        if (request.getName() != null && !place.getName().trim().equalsIgnoreCase(request.getName().trim()))
            return true;
        if (request.getDescription() != null && !place.getDescription().trim().equals(request.getDescription().trim()))
            return true;



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
