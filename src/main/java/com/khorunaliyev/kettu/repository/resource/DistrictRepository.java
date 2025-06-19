package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.DistrictInfo;
import com.khorunaliyev.kettu.entity.resources.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<DistrictInfo> findByRegion_Id(Long id);
}