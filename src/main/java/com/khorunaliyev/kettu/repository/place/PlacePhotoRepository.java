package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.PlacePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacePhotoRepository extends JpaRepository<PlacePhoto, Long> {
}