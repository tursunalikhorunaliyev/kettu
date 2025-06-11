package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.RegionInfo;
import com.khorunaliyev.kettu.entity.resources.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<RegionInfo> findByCountry_Id(Long id);
}