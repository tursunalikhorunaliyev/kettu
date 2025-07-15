package com.khorunaliyev.kettu.services.place;
import com.khorunaliyev.kettu.dto.mapper.PlaceMappers;
import com.khorunaliyev.kettu.dto.reponse.place.*;
import com.khorunaliyev.kettu.dto.request.place.LatLongRequest;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceMappers placeMappers;

    @Cacheable("places")
    public ResponseEntity<List<PlaceDTO>> getAllPlaces() {
        List<PlaceDTO> places = placeRepository.findAllBy().stream().map(placeMappers::toDto).toList();
        return ResponseEntity.ok(places);
    }

//    public ResponseEntity<List<PlaceDTO>> getNearbyPlaces(LatLongRequest latLongRequest){
//
//    }

}
