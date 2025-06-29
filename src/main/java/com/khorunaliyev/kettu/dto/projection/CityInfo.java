package com.khorunaliyev.kettu.dto.projection;

import com.khorunaliyev.kettu.entity.resources.Region;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.City}
 */
public interface CityInfo {
    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Long getId();

    String getName();

    Integer getActiveItemCount();
}