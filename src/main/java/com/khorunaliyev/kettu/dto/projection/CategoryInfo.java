package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.khorunaliyev.kettu.entity.resources.SubCategory;

/**
 * Projection for {@link SubCategory}
 */

@JsonPropertyOrder({ "id", "name", "item_count"})
public interface CategoryInfo {
    Integer getId();

    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();
}