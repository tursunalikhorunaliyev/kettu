package com.khorunaliyev.kettu.dto.request.region;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewRegionRequest extends RegionNameRequest{
    @NotNull
    @Positive
    private Long countryId;
}
