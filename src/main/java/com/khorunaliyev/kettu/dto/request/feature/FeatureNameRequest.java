package com.khorunaliyev.kettu.dto.request.feature;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureNameRequest {
    @NotNull
    @Size(min = 1, max = 20)
    private String name;
}
