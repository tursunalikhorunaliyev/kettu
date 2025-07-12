package com.khorunaliyev.kettu.dto.request.place;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceLocationRequest {
    @NotNull
    @Positive
    private Long countryId;
    @NotNull
    @Positive
    private Long regionId;
    @NotNull
    @Positive
    private Long districtId;
    @NotNull
    @Positive
    private double lat_;
    @NotNull
    @Positive
    private double long_;
}
