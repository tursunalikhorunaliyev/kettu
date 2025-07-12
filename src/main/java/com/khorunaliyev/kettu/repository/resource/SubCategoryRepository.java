package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.SubCategoryInfo;
import com.khorunaliyev.kettu.entity.resources.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
  @Query("SELECT sb FROM SubCategory sb WHERE sb.category.id = :categoryId")
  List<SubCategoryInfo> findByCategoryId(@Param("categoryId") Long categoryId);
}