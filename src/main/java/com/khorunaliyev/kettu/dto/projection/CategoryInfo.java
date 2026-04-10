package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Category}
 */

@JsonPropertyOrder({ "id", "name", "item_count"})
public interface CategoryInfo {
    Integer getId();

    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();
}