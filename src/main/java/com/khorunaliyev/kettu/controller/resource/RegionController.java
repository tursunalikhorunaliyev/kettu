package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.NewRegionRequest;
import com.khorunaliyev.kettu.dto.request.RegionNameRequest;
import com.khorunaliyev.kettu.services.resource.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/region")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid NewRegionRequest request) {
        return regionService.createRegion(request.getCountryId(), request.getName());
    }

    @GetMapping("/")
    public ResponseEntity<Response> byCountry(@RequestParam Long country){
        return regionService.getByCountry(country);
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("country_id") Long countryId, @RequestParam("file") MultipartFile file) {
        return regionService.importFromExcel(countryId, file);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> updateRegion(@PathVariable("id") Long regionId, @RequestBody @Valid RegionNameRequest request){
        return  regionService.updateRegionName(regionId, request.getName());
    }
}
