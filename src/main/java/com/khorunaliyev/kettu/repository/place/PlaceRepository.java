package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
}