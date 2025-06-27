package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.RegionInfo;
import com.khorunaliyev.kettu.entity.resources.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    @Query("SELECT r FROM Region r WHERE r.country.id = :countryId")
    List<RegionInfo> findByCountryId(@Param("countryId") Long countryId);
}