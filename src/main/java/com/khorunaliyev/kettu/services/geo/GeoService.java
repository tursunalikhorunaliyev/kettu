package com.khorunaliyev.kettu.services.geo;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.geo.GeoDataFromPoint;
import com.khorunaliyev.kettu.entity.resources.District;
import com.khorunaliyev.kettu.entity.resources.Region;
import com.khorunaliyev.kettu.repository.resource.DistrictRepository;
import com.khorunaliyev.kettu.repository.resource.RegionRepository;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
@AllArgsConstructor
public class GeoService {
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final MessageSource messageSource;

    public GeoDataFromPoint geoData(double latitude, double longitude) {
        final Tuple data = districtRepository.findDistrictByGeoData(latitude, longitude);
        if (data == null) {
            throw new ResourceNotFoundException("District not found");
        }

        Integer regionId = data.get("region_id", Integer.class);
        String regionSlug = data.get("region_name", String.class);
        String regionName = messageSource.getMessage("region." + regionSlug, null, LocaleContextHolder.getLocale());

        Integer districtId = data.get("district_id", Integer.class);
        String districtSlug = data.get("district_name", String.class);
        String districtName = messageSource.getMessage("district." + districtSlug, null, LocaleContextHolder.getLocale());


        return new GeoDataFromPoint(new GeoDataFromPoint.Point(latitude, longitude),new GeoDataFromPoint.IdSlugName(regionId,regionSlug, regionName), new GeoDataFromPoint.IdSlugName(districtId,districtSlug, districtName));
    }

    @Cacheable(value = "regionSlugsCache")
    public List<String> regionSlugs(){
        return regionRepository.findAll().stream().map(Region::getName).toList();
    }

    @Cacheable(value = "regionDistrictCache")
    public List<String> regionDistrictCache(String region){
        return districtRepository.findByRegion_Name(region).stream().map(District::getName).toList();
    }
}
