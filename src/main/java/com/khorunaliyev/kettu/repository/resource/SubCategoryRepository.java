package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.entity.resources.SubCategory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO subcategory_tags (subcategory_id, tag_id) SELECT :subcategory_id, UNNEST(CAST(:tag_ids AS int[])) ON CONFLICT (subcategory_id, tag_id) DO NOTHING", nativeQuery = true)
    void assignTagsBatch(@Param("subcategory_id") Integer subcategoryId, @Param("tag_ids") Integer[] tagIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM subcategory_tags WHERE subcategory_id = :subcategory_id AND tag_id = ANY(CAST(:tag_ids AS int[]))", nativeQuery = true)
    void unassignTagsBatch(@Param("subcategory_id") Integer subcategoryId, @Param("tag_ids") Integer[] tagIds);

    @EntityGraph(attributePaths = {"tags"})
    Optional<SubCategory> findWithTagsById(Integer id);

    @Query(value = "select count(*) from subcategory_tags where subcategory_id = :subcategory_id and tag_id IN(:tag_ids)", nativeQuery = true)
    int countByCategoryAndTags(@Param("subcategory_id") Integer categoryId, @Param("tag_ids") Set<Integer> tagIds);
}