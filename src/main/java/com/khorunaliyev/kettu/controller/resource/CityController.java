package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.CityNameRequest;
import com.khorunaliyev.kettu.dto.request.NewCityRequest;
import com.khorunaliyev.kettu.services.resource.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/city")
@RequiredArgsConstructor
public class CityController {
    private final CityService cityService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid NewCityRequest request) {
        return cityService.createCity(request.getRegionId(), request.getName());
    }

    @GetMapping("/")
    public ResponseEntity<Response> byRegion(@RequestParam Long region){
        return cityService.getByRegion(region);
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("region_id") Long regionId, @RequestParam("file") MultipartFile file) {
        return cityService.importFromExcel(regionId, file);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> updateRegion(@PathVariable("id") Long cityId, @RequestBody @Valid CityNameRequest request){
        return  cityService.updateCityName(cityId, request.getName());
    }
}
