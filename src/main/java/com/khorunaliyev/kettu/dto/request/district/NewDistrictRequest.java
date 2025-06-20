package com.khorunaliyev.kettu.dto.request.district;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewDistrictRequest extends DistrictNameRequest {
    @NotNull
    @Positive
    private Long regionId;
}
