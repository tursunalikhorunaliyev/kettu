package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.District}
 */
@JsonPropertyOrder({ "id", "name", "item_count"})
public interface DistrictInfo {
    Integer getId();

    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();
}