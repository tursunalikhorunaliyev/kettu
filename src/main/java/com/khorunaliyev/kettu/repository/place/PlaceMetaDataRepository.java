package com.khorunaliyev.kettu.repository.place;

import com.khorunaliyev.kettu.entity.place.PlaceMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceMetaDataRepository extends JpaRepository<PlaceMetaData, Long> {
}