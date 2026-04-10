package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.khorunaliyev.kettu.entity.resources.Country;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Region}
 */
@JsonPropertyOrder({ "id", "name", "item_count"})
public interface RegionInfo {

    Integer getId();

    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();
}