package com.khorunaliyev.kettu.dto.request.place;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PlaceUpdateRequest {
    @Size(min = 1, max = 50)
    private String name;

    @Size(min = 1, max = 500)
    private String description;

    @NotEmpty
    private List<PlacePhotoRequest> placePhotos;

    private PlaceLocationRequest placeLocation;

    private PlaceMetaDataRequest placeMetaData;

    private List<Long> nearbyThings;
}
