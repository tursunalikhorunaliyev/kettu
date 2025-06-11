package com.khorunaliyev.kettu.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegionNameRequest {
    @NotNull
    @Size(min = 1, max = 85)
    private String name;
}
