package com.khorunaliyev.kettu.dto.projection;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.District}
 */
public interface DistrictInfo {
    Integer getId();

    String getName();

    Integer getActiveItemCount();
}