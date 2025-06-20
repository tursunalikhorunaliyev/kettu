package com.khorunaliyev.kettu.dto.request.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryNameRequest {
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
}
