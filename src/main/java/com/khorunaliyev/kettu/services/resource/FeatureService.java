package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.Feature;
import com.khorunaliyev.kettu.repository.resource.FeatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeatureService {
    private final FeatureRepository featureRepository;

    public ResponseEntity<Response> createFeature(String name){
        Feature feature = new Feature();
        feature.setName(name);
        featureRepository.save(feature);
        return new ResponseEntity<>(new Response("Feature created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> updateFeatureName(Long id, String name){
        Feature feature = featureRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feature not found"));
        feature.setName(name);
        featureRepository.save(feature);
        return ResponseEntity.ok(new Response("Feature updated", null));
    }

    public ResponseEntity<Response> getAll(){
        return ResponseEntity.ok(new Response("Features", featureRepository.findAllBy()));
    }
}
