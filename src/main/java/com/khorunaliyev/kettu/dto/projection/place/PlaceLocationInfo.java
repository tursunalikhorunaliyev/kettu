package com.khorunaliyev.kettu.dto.projection.place;

import com.khorunaliyev.kettu.dto.projection.CountryInfo;
import com.khorunaliyev.kettu.dto.projection.DistrictInfo;
import com.khorunaliyev.kettu.dto.projection.RegionInfo;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.place.PlaceLocation}
 */
public interface PlaceLocationInfo {
    Long getId();

    CountryInfo getCountry();

    RegionInfo getRegion();

    DistrictInfo getDistrict();

    @Value("#{@projectionUtils.convertPoint(target.point)}")
    Map<String, Double> getPoint();
}