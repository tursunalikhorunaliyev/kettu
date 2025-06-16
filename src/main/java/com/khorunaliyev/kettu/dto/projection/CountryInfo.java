package com.khorunaliyev.kettu.dto.projection;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Country}
 */
public interface CountryInfo {

    Long getId();

    String getName();

    Integer getActiveItemCount();

    List<RegionInfo> getRegions();
}