package com.khorunaliyev.kettu.dto.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.resources.Category}
 */
public interface CategoryInfo {
    Integer getId();

    String getName();

    @Value("#{target.active_item_count}")
    Integer getActiveItemCount();
}