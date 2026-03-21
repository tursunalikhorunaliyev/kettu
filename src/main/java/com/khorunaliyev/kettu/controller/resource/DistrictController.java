package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.district.DistrictNameRequest;
import com.khorunaliyev.kettu.dto.request.district.NewDistrictRequest;
import com.khorunaliyev.kettu.services.resource.DistrictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/resources/districts")
@RequiredArgsConstructor
public class DistrictController {
    private final DistrictService districtService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid NewDistrictRequest request) {
        return districtService.createCity(request.getRegionId(), request.getName());
    }

    @GetMapping("/")
    public ResponseEntity<Response> byRegion(@RequestParam Long region){
        return districtService.getByRegion(region);
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromGeoJson(@RequestParam("region_id") Integer regionId, @RequestParam("file") MultipartFile file) throws IOException {
        return districtService.importFromGeoJson(regionId, file);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> updateRegion(@PathVariable("id") Integer cityId, @RequestBody @Valid DistrictNameRequest request){
        return  districtService.updateCityName(cityId, request.getName());
    }

    @GetMapping("/geo-data")
    public ResponseEntity<Response> geoData(@RequestParam double latitude, @RequestParam double longitude){
        return districtService.reverseGeoDataFromPoint(latitude, longitude);
    }
}
