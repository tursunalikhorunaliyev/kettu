package com.khorunaliyev.kettu.dto.request.place;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlacePhotoRequest {
    @NotNull
    @NotBlank
    private String imageName;
    @NotNull
    private boolean isMain;
}
