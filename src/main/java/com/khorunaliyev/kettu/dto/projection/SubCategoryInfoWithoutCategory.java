package com.khorunaliyev.kettu.dto.projection;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.SubCategory}
 */
public interface SubCategoryInfoWithoutCategory {
    Integer getId();

    String getName();

    Integer getActiveItemCount();
}