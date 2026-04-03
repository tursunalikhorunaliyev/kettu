package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.CategoryInfo;
import com.khorunaliyev.kettu.entity.resources.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO category_tags (category_id, tag_id) SELECT :category_id, UNNEST(CAST(:tag_ids AS int[])) ON CONFLICT (category_id, tag_id) DO NOTHING", nativeQuery = true)
    void assignTagsBatch(@Param("category_id") Integer categoryId, @Param("tag_ids") Integer[] tagIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM category_tags WHERE category_id = :category_id AND tag_id = ANY(CAST(:tag_ids AS int[]))", nativeQuery = true)
    void unassignTagsBatch(@Param("category_id") Integer categoryId, @Param("tag_ids") Integer[] tagIds);

    @EntityGraph(attributePaths = {"tags"})
    Optional<Category> findWithTagsById(Integer id);

    @Query(value = "select count(*) from category_tags where category_id = :category_id and tag_id IN(:tag_ids)", nativeQuery = true)
    int countByCategoryAndTags(@Param("category_id") Integer categoryId, @Param("tag_ids") Set<Integer> tagIds);
}