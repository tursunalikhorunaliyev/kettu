package com.khorunaliyev.kettu.dto.projection;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Region}
 */
public interface RegionInfo {
    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Long getId();

    String getName();

    Integer getActiveItemCount();

    List<CityInfo> getCities();
}