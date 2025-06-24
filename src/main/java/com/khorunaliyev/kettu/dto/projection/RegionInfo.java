package com.khorunaliyev.kettu.dto.projection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Region}
 */
public interface RegionInfo {

    Long getId();

    String getName();

    Integer getActiveItemCount();
}