package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.entity.resources.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
  List<SubCategory> findByCategory_Id(Long id);
}