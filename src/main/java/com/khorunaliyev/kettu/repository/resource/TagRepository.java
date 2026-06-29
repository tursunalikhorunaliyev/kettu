package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.TagInfo;
import com.khorunaliyev.kettu.entity.resources.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query(value = "SELECT t.* FROM tag t " +
            "JOIN subcategory_tags sct ON t.id = sct.tag_id " +
            "JOIN sub_category sc ON sc.id = sct.subcategory_id " +
            "WHERE sc.name = :subName " +
            "ORDER BY t.id ASC",
            nativeQuery = true)
    List<TagInfo> findAllBySubCategoryNameNative(@Param("subName") String subName);
}