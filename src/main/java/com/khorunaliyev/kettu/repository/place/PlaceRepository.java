package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.dto.projection.FeatureInfo;
import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.entity.place.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    @EntityGraph(attributePaths = {
            "location.country",
            "location.region",
            "location.district",
            "metaData.category",
            "metaData.subCategory",
            "metaData.feature",
            "photos",
            "nearbyThings",
            "likedUsers",
            "visitedUsers"
    })
    @Query("""
            SELECT p FROM Place p
            WHERE (:categoryId IS NULL OR p.metaData.category.id = :categoryId)
            AND (:subcategoryId IS NULL OR p.metaData.subCategory.id = :subcategoryId)
            AND (:districtId IS NULL OR p.location.district.id = :districtId)
            AND (:regionId IS NULL OR p.location.region.id = :regionId)
            AND (:countryId IS NULL OR p.location.country.id = :countryId)
            """)
    Page<PlaceInfo> findAllBy(@Param("categoryId") Long categoryId, @Param("subcategoryId") Long subcategoryId, @Param("districtId") Long districtId, @Param("regionId") Long regionId, @Param("countryId") Long countryId, Pageable pageable);

    @Query("""
    SELECT p FROM Place p
    JOIN p.metaData md
    WHERE
        (:categoryId IS NOT NULL OR :subCategoryId IS NOT NULL) AND
        (:categoryId IS NULL OR md.category.id = :categoryId) AND
        (:subCategoryId IS NULL OR md.subCategory.id = :subCategoryId)
    """)
    List<Place> findPlacesByMetaData(@Param("categoryId") Long categoryId,
                                     @Param("subCategoryId") Long subCategoryId);
}