package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.RegionInfo;
import com.khorunaliyev.kettu.entity.resources.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    @Query("SELECT r FROM Region r WHERE r.country.name = :country")
    List<RegionInfo> findByCountryName(@Param("country") String country);

}