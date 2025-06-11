package com.khorunaliyev.kettu.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewRegionRequest extends RegionNameRequest{
    @NotNull
    private Long countryId;
}
