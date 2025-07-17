package com.khorunaliyev.kettu.services.place;
import com.khorunaliyev.kettu.component.PlaceMappers;
import com.khorunaliyev.kettu.dto.reponse.place.*;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMappers placeMappers;

    @Cacheable("places")
    public ResponseEntity<Page<PlaceDTO>> getAllPlaces(Long categoryId, Long districtId, Long regionId, Long countryId, Pageable pageable) {
        Page<PlaceDTO> places = placeRepository.findAllBy(categoryId, districtId, regionId, countryId, pageable).map(placeMappers::toDto);
        return ResponseEntity.ok(places);
    }

//    public ResponseEntity<List<PlaceDTO>> getNearbyPlaces(LatLongRequest latLongRequest){
//
//    }


}
