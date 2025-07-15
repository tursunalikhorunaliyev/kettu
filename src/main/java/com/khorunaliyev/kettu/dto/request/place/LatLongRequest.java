package com.khorunaliyev.kettu.dto.request.place;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LatLongRequest {
    @NotNull
    @Positive
    private Double lat_;

    @NotNull
    @Positive
    private Double long_;
}
