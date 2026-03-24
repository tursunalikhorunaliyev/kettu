package com.khorunaliyev.kettu.controller.geo;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.services.geo.GeoService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/geo-data")
@RequiredArgsConstructor
public class GeoDataController {

    private final GeoService geoService;

    @GetMapping
    public ResponseEntity<Response> geoDataFromPoint(@RequestParam("lat") double latitude, @RequestParam("long") double longitude){
        return ResponseEntity.ok(new Response("Success",geoService.geoData(latitude, longitude)));
    }
}
