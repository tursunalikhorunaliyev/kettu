package com.khorunaliyev.kettu.dto.projection;

import com.khorunaliyev.kettu.entity.resources.Tag;

import java.util.List;

public interface SubcategoryDetailInfo extends SubCategoryInfo {
    List<Tag> getTags();
}
