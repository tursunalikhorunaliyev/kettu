package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Country}
 */
@JsonPropertyOrder({ "id", "name", "item_count"})
public interface CountryInfo {

    Integer getId();

    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();

}