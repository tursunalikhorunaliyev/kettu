package com.khorunaliyev.kettu.dto.request.place;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isMain")
    private Boolean isMain;
}
