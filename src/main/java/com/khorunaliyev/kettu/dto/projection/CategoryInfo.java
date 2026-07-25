package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.khorunaliyev.kettu.component.Translator;
import com.khorunaliyev.kettu.entity.resources.SubCategory;

/**
 * Projection for {@link SubCategory}
 */

@JsonPropertyOrder({ "id", "slug", "title", "item_count"})
public interface CategoryInfo {
    Integer getId();

    @JsonProperty("slug")
    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();

    @JsonProperty("title")
    default String getTitle() {return Translator.translate("category."+getName());}
}