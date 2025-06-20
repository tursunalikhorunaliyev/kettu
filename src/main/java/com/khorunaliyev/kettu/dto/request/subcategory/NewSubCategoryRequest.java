package com.khorunaliyev.kettu.dto.request.subcategory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewSubCategoryRequest extends SubCategoryNameRequest{
    @NotNull
    @Positive
    private Long categoryId;
}
