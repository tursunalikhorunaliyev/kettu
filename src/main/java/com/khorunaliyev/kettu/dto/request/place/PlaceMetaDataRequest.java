package com.khorunaliyev.kettu.dto.request.place;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceMetaDataRequest {
    @NotNull
    @Positive
    private Long featureId;
    @NotNull
    @Positive
    private Long categoryId;
    @NotNull
    @Positive
    private Long subcategoryId;
}
