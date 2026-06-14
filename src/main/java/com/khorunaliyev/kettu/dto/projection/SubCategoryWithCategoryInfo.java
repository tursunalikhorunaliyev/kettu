package com.khorunaliyev.kettu.dto.projection;


public interface SubCategoryWithCategoryInfo extends SubCategoryInfo {
    CategoryInfo getCategory();
}
