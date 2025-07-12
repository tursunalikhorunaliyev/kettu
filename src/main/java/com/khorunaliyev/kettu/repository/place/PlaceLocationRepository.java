package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.PlaceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceLocationRepository extends JpaRepository<PlaceLocation, Long> {
}