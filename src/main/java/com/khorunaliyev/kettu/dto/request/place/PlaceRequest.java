package com.khorunaliyev.kettu.dto.request.place;

import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.lang.annotation.Native;
import java.util.List;

@Getter
@Setter
public class PlaceRequest {
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @Size(min = 20, max = 500)
    private String description;

    @NotNull
    @Positive
    private Long category_id;

    @NotNull
    @NotEmpty
    private List<PlacePhotoRequest> place_photos;

    @NotNull
    @Size(min = 3, max = 10)
    private List<Integer> tags;

    @NotNull
    private PlaceLocationRequest place_location;
}
