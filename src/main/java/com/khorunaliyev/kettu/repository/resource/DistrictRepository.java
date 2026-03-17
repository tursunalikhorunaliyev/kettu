package com.khorunaliyev.kettu.repository.resource;

import com.khorunaliyev.kettu.dto.projection.DistrictInfo;
import com.khorunaliyev.kettu.entity.resources.District;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    List<DistrictInfo> findByRegion_Id(Long id);

    @Query(value = "select * from district as d  where ST_Contains(d.geom, ST_SetSRID(ST_Point(:lng, :lat),4326))", nativeQuery = true)
    Tuple findDistrictByGeoData(@Param("lat") double latitude, @Param("lng") double longitude);
}