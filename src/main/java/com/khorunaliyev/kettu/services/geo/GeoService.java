package com.khorunaliyev.kettu.services.geo;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.geo.GeoDataFromPoint;
import com.khorunaliyev.kettu.repository.resource.DistrictRepository;
import jakarta.persistence.Tuple;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class GeoService {
    private final DistrictRepository districtRepository;

    public GeoDataFromPoint geoData(double latitude, double longitude){
        final Tuple data = districtRepository.findDistrictByGeoData(latitude, longitude);
        if(data == null){
            throw new ResourceNotFoundException("District not found");
        }

        Integer regionId = data.get("region_id", Integer.class);
        String regionName = data.get("region_name", String.class);

        Integer districtId = data.get("district_id", Integer.class);
        String districtName = data.get("district_name", String.class);


        return new GeoDataFromPoint(new GeoDataFromPoint.IdNameResponse(regionId,regionName),new GeoDataFromPoint.IdNameResponse(districtId,districtName));
    }
}
