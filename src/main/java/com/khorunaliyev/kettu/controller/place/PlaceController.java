package com.khorunaliyev.kettu.controller.place;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateRequest;
import com.khorunaliyev.kettu.dto.request.place.PlaceUpdateStatusRequest;
import com.khorunaliyev.kettu.entity.enums.PlaceStatus;
import com.khorunaliyev.kettu.entity.place.Place;
import com.khorunaliyev.kettu.services.place.ChangePlaceStatusService;
import com.khorunaliyev.kettu.services.place.CreatePlaceService;
import com.khorunaliyev.kettu.services.place.PlaceService;
import com.khorunaliyev.kettu.services.place.UpdatePlaceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/places")
@AllArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final UpdatePlaceService updatePlaceService;
    private final ChangePlaceStatusService changePlaceStatusService;
    private final CreatePlaceService createPlaceService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> create(@RequestPart("data") @Valid PlaceRequest request, @RequestPart("main_photo") MultipartFile mainPhoto, @RequestPart(value = "additional_photos", required = false) List<MultipartFile> additionalPhotos) throws IOException {
        return createPlaceService.createPlace(request, mainPhoto, additionalPhotos);
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
    public ResponseEntity<Response> getPlaces(@RequestParam(value = "status",required = false) PlaceStatus status,
                                              @RequestParam(value = "name",required = false) String name,
                                              @RequestParam(value = "category_id", required = false) Integer categoryId,
                                              @RequestParam(value = "tag_ids",required = false) List<Integer> tagsId,
                                              @RequestParam(value = "region_id",required = false) Integer regionId,
                                              @RequestParam(value = "district_id",required = false) Integer districtId) {
        return placeService.getAllPlaces(status, name, categoryId, tagsId, regionId, districtId);
    }

    @GetMapping("/uploading")
    public ResponseEntity<Response> uploading(){
        return placeService.checkUploadingPlaces();
    }

}
