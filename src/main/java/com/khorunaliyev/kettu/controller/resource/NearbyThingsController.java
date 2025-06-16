package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.NearbyNameRequest;
import com.khorunaliyev.kettu.services.resource.NearbyThingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/nearby-things")
@RequiredArgsConstructor
public class NearbyThingsController {

    private final NearbyThingsService nearbyThingsService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestParam("name") String name, @RequestParam("icon") MultipartFile file){
        return nearbyThingsService.createThing(name, file);
    }

    @PostMapping("/{id}/update-name")
    public ResponseEntity<Response> updateName(@PathVariable("id") Long id, @RequestBody @Valid NearbyNameRequest request){
        return nearbyThingsService.updateName(id, request.getName());
    }

    @PostMapping("/{id}/update-image")
    public ResponseEntity<Response> updateIcon(@PathVariable("id") Long id, @RequestParam("icon") MultipartFile file){
        return nearbyThingsService.updateIcon(id, file);
    }
}
