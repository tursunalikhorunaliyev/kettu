package com.khorunaliyev.kettu.controller.place;

import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.PlaceDTO;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateRequest;
import com.khorunaliyev.kettu.services.place.PlaceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/places")
@AllArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping("/save")
    public ResponseEntity<Response> create(@RequestBody @Valid PlaceRequest request){
        return placeService.createPlace(request);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> update(@PathVariable("id") Long placeId, @RequestBody @Valid PlaceUpdateRequest request){
        System.out.println("Request");
        System.out.println(request.getPlacePhotos().get(0).getImageName());
        System.out.println(request.getPlacePhotos().get(0).getIsMain());
        return placeService.update(placeId, request);
    }

    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getPlaces(){
        return placeService.getAllPlaces();
    }

}
