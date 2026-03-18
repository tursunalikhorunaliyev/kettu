package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.DistrictInfo;
import com.khorunaliyev.kettu.entity.resources.District;
import com.khorunaliyev.kettu.util.query.DistrictQuery;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<DistrictInfo> findByRegion_Id(Long id);

    @Query(value = DistrictQuery.FIND_WITH_POINT, nativeQuery = true)
    Tuple findDistrictByGeoData(@Param("lat") double latitude, @Param("lng") double longitude);
}