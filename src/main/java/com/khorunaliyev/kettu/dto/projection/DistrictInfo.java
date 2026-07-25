package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.khorunaliyev.kettu.component.Translator;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.District}
 */
@JsonPropertyOrder({ "id", "name", "title", "item_count"})
public interface DistrictInfo {
    Integer getId();

    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();

    @JsonProperty("title")
    default String getTitle() {return Translator.translate("district."+getName());}
}