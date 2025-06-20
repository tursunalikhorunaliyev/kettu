package com.khorunaliyev.kettu.dto.request.category;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCategoryRequest extends CategoryNameRequest{
    @NotNull
    @Positive
    private Long featureId;
}
