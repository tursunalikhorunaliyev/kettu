package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.component.PlaceMappers;
import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.projection.PlaceMetaDataInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.place.*;
import com.khorunaliyev.kettu.repository.place.PlaceMetaDataRepository;
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

    @Cacheable("places")
    public ResponseEntity<Response> getAllPlaces(Long categoryId,Long districtId, Long regionId, Long countryId, Pageable pageable) {



         if((countryId!=null || regionId!=null || districtId!=null) && (categoryId==null)){
            if(countryId!=null && countryId<1){
                throw new ResourceNotFoundException("Resource not found. Bad element");
            }
            else if(regionId!=null && regionId<1){
                throw new ResourceNotFoundException("Resource not found. Bad element");
            }
            else if(districtId!= null && districtId<1){
                throw new ResourceNotFoundException("Resource not found. Bad element");
            }
             //find with place location
        }
        else{

             //find with place itself
        }
        return ResponseEntity.ok(new Response("ok","ok"));

    }


//    public ResponseEntity<List<PlaceDTO>> getNearbyPlaces(LatLongRequest latLongRequest){
//
//    }


}
