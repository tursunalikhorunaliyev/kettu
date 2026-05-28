package com.khorunaliyev.kettu.repository.place;
import com.khorunaliyev.kettu.dto.projection.place.PlaceInfo;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long>, JpaSpecificationExecutor<Place> {
    @Modifying
    @Transactional
    @Query("UPDATE Place p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") PlaceStatus status);

    @Override
    @EntityGraph(attributePaths = {
            "category",
            "tags",
            "photos",
            "location",
            "location.region",
            "location.district",
            "location.country",
            "createdBy"
    })
    List<Place> findAll(Specification<Place> spec);
}