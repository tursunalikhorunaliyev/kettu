package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "slug", "title", "item_count", "category"})
public interface SubCategoryWithCategoryInfo extends SubCategoryInfo {
    CategoryInfo getCategory();
}
