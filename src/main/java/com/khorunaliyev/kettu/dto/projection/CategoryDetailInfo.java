package com.khorunaliyev.kettu.dto.projection;

import java.util.List;

public interface CategoryDetailInfo extends CategoryInfo {
    List<SubCategoryInfoWithoutCategory> getSubCategories();
}
