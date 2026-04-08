package com.khorunaliyev.kettu.dto.projection.place;

import com.khorunaliyev.kettu.dto.projection.CountryInfo;
import com.khorunaliyev.kettu.dto.projection.DistrictInfo;
import com.khorunaliyev.kettu.dto.projection.RegionInfo;
import org.locationtech.jts.geom.Point;

/**
 * Projection for {@link com.khorunaliyev.kettu.entity.place.PlaceLocation}
 */
public interface PlaceLocationInfo {
    Long getId();

    Point getPoint();

    CountryInfo getCountry();

    RegionInfo getRegion();

    DistrictInfo getDistrict();
}