package com.khorunaliyev.kettu.dto.projection;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Tag}
 */
@JsonPropertyOrder({ "id", "name"})
public interface TagInfo {
    Integer getId();

    String getName();
}