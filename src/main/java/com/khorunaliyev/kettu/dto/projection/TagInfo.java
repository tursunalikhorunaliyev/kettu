package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.khorunaliyev.kettu.component.Translator;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Tag}
 */
@JsonPropertyOrder({ "id", "name", "title"})
public interface TagInfo {
    Integer getId();

    String getName();

    @JsonProperty("title")
    default String getTitle() {return Translator.translate("tag."+getName());}
}