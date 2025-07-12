package com.khorunaliyev.kettu.dto.request.subcategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubCategoryNameRequest {
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
}
