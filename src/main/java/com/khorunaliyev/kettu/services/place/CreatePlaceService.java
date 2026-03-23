package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.place.PlaceRequest;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import com.khorunaliyev.kettu.repository.resource.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreatePlaceService {

    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final CategoryRepository categoryRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    @CacheEvict(value = "places", allEntries = true)
    public ResponseEntity<Response> createPlace(PlaceRequest request, List<MultipartFile> files) {

        if(!categoryRepository.existsById(request.getCategory_id())){
            throw new ResourceNotFoundException("Category not found");
        }

        Set<Integer> tagsSet = new HashSet<>(request.getTags());

        if(tagsSet.size() != categoryRepository.countByCategoryAndTags(request.getCategory_id(),tagsSet)){
            throw new ResourceNotFoundException("Some tags not found");
        }




    }
}
