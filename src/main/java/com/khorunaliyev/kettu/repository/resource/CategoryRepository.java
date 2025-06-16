package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.CategoryInfo;
import com.khorunaliyev.kettu.dto.projection.CountryInfo;
import com.khorunaliyev.kettu.entity.resources.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<CategoryInfo> findAllBy();
}