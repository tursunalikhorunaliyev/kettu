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

@RestController
@RequestMapping("api/resources/city")
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
    public ResponseEntity<Response> importFromExcel(@RequestParam("region_id") Long regionId, @RequestParam("file") MultipartFile file) {
        return districtService.importFromExcel(regionId, file);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> updateRegion(@PathVariable("id") Long cityId, @RequestBody @Valid DistrictNameRequest request){
        return  districtService.updateCityName(cityId, request.getName());
    }
}
