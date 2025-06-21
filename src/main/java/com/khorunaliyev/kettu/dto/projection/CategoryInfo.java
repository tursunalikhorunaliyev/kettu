package com.khorunaliyev.kettu.dto.projection;

import java.util.List;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Category}
 */
public interface CategoryInfo {
    Long getId();

    String getName();

    Integer getActiveItemCount();

    List<SubCategory> getSubCategories();

    /**
     * Projection for {@link com.khorunaliyev.kettu.entity.resources.SubCategory}
     */
    interface SubCategory {
        Long getId();

        String getName();

        Integer getActiveItemCount();
    }
}