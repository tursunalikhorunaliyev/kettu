package com.khorunaliyev.kettu.controller.place;

import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.PlaceDTO;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateStatusRequest;
import com.khorunaliyev.kettu.services.place.ChangePlaceStatusService;
import com.khorunaliyev.kettu.services.place.CreatePlaceService;
import com.khorunaliyev.kettu.services.place.PlaceService;
import com.khorunaliyev.kettu.services.place.UpdatePlaceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/places")
@AllArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final UpdatePlaceService updatePlaceService;
    private final ChangePlaceStatusService changePlaceStatusService;
    private final CreatePlaceService createPlaceService;

    @PostMapping("/save")
    public ResponseEntity<Response> create(@RequestBody @Valid PlaceRequest request) {
        return createPlaceService.createPlace(request);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> update(@PathVariable("id") Long placeId, @RequestBody @Valid PlaceUpdateRequest request) {
        return updatePlaceService.update(placeId, request);
    }

    @PostMapping("{id}/update-status")
    public ResponseEntity<Response> updateStatus(@PathVariable("id") Long placeID, @RequestBody @Valid PlaceUpdateStatusRequest request) {
        return changePlaceStatusService.changePlaceStatus(placeID, request);
    }

    @GetMapping
    public ResponseEntity<Page<PlaceDTO>> getPlaces(@RequestParam(required = false) Long categoryId,
                                                    @RequestParam(required = false) Long districtId,
                                                    @RequestParam(required = false) Long regionId,
                                                    @RequestParam(required = false) Long countryId,
                                                    Pageable pageable
    ) {
        return placeService.getAllPlaces(categoryId, districtId, regionId, countryId, pageable);
    }

}
