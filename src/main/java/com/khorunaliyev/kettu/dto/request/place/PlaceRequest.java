package com.khorunaliyev.kettu.dto.request.place;

import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Native;
import java.util.List;

@Getter
@Setter
public class PlaceRequest {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @Size(min = 1, max = 500)
    private String description;

    @NotNull
    @NotEmpty
    private List<PlacePhoto> placePhotos;

    @NotNull
    private PlaceLocationRequest placeLocation;

    @NotNull
    private List<Long> nearbyThings;
}
