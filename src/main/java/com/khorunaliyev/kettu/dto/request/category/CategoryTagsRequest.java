package com.khorunaliyev.kettu.dto.request.category;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryTagsRequest {
    @NotNull(message = "Maydon null bo'lishi mumkin emas")
    private Integer category_id;

    @NotNull(message = "Maydon null bo'lishi mumkin emas")
    @NotEmpty(message = "Maydon bo'sh bo'lishi mumkin emas")
    private List<Integer> tag_ids;
}
