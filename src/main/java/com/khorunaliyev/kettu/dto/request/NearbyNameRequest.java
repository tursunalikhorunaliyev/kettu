package com.khorunaliyev.kettu.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearbyNameRequest {
    @NotNull
    @NotBlank
    @Max(100)
    private String name;
}
