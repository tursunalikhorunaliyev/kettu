package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.dto.projection.FeatureInfo;
import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.entity.place.Place;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<PlaceInfo> findAllBy();
}