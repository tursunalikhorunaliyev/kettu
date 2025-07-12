package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacePhotoRepository extends JpaRepository<PlacePhoto, Long> {
    @Query("SELECT p FROM PlacePhoto p WHERE p.place.id = :placeId AND p.image IN :imageNames")
    List<PlacePhoto> findByPlaceIdAndImageIn(@Param("placeId") Long placeId, @Param("imageNames") List<String> imageNames);
}