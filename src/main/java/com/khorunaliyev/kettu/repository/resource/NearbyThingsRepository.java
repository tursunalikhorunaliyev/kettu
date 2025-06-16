package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.NearbyThingsInfo;
import com.khorunaliyev.kettu.entity.resources.NearbyThings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NearbyThingsRepository extends JpaRepository<NearbyThings, Long> {
    List<NearbyThingsInfo> findAllBy();
}