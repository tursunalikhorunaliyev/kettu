package com.khorunaliyev.kettu.repository.place;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Place p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") PlaceStatus status);

    @EntityGraph(attributePaths = {
            "category",
            "tags",
            "photos",
            "location.region",
            "location.district",
            "location.country",
            "createdBy.name",
            "createdBy.image"
    })
    @Query("SELECT p FROM Place p")
    Page<Place> findAllWithDetails(Pageable pageable);
}