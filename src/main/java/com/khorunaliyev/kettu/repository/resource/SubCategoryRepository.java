package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.SubCategoryInfo;
import com.khorunaliyev.kettu.dto.projection.SubCategoryWithCategoryInfo;
import com.khorunaliyev.kettu.dto.projection.SubcategoryDetailInfo;
import com.khorunaliyev.kettu.dto.projection.TagInfo;
import com.khorunaliyev.kettu.entity.resources.SubCategory;
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
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO subcategory_tags (subcategory_id, tag_id) SELECT :subcategory_id, UNNEST(CAST(:tag_ids AS int[])) ON CONFLICT (subcategory_id, tag_id) DO NOTHING", nativeQuery = true)
    void assignTagsBatch(@Param("subcategory_id") Integer subcategoryId, @Param("tag_ids") Integer[] tagIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM subcategory_tags WHERE subcategory_id = :subcategory_id AND tag_id = ANY(CAST(:tag_ids AS int[]))", nativeQuery = true)
    void unassignTagsBatch(@Param("subcategory_id") Integer subcategoryId, @Param("tag_ids") Integer[] tagIds);

    @EntityGraph(attributePaths = {"category", "tags"})
    Optional<SubcategoryDetailInfo> findWithTagsById(Integer id);

    @Query(value = "select count(*) from subcategory_tags where subcategory_id = :subcategory_id and tag_id IN(:tag_ids)", nativeQuery = true)
    int countByCategoryAndTags(@Param("subcategory_id") Integer categoryId, @Param("tag_ids") Set<Integer> tagIds);

    @Query(value = "SELECT t.* FROM tag t JOIN subcategory_tags sct ON t.id = sct.tag_id JOIN sub_category sc ON sc.id = sct.subcategory_id WHERE sc.name = :slug ORDER BY t.id ASC", nativeQuery = true)
    Set<TagInfo> findTagsBySubCategorySlug(@Param("slug") String slug);


    @EntityGraph(attributePaths = {"category"})
    List<SubCategoryWithCategoryInfo> findAllBy();

    List<SubCategoryInfo> findByCategory_Name(String name);

    boolean existsByName(String name);

    Optional<SubCategory> findByName(String name);

}