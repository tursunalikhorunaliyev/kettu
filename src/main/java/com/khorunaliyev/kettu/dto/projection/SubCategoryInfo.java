package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.khorunaliyev.kettu.component.Translator;
import org.springframework.beans.factory.annotation.Value;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.SubCategory}
 */
@JsonPropertyOrder({"id", "name", "activeItemCount",})
public interface SubCategoryInfo {
    Integer getId();

    @JsonProperty("slug")
    String getName();

    @JsonProperty("item_count")
    Integer getActiveItemCount();

    default String getTitle() {
        return Translator.translate(getName());
    }

}