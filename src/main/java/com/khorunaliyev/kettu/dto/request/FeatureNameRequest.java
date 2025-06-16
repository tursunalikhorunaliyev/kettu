package com.khorunaliyev.kettu.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeatureNameRequest {

    @NotNull
    @NotBlank
    @Max(20)
    private String name;
}
