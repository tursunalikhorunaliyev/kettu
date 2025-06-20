package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.feature.FeatureNameRequest;
import com.khorunaliyev.kettu.services.resource.FeatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/resources/feature")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureService featureService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid FeatureNameRequest request){
        return featureService.createFeature(request.getName());
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> update(@PathVariable("id") Long featureId, @RequestBody() @Valid FeatureNameRequest request){
        return featureService.updateFeatureName(featureId, request.getName());
    }

    @GetMapping("/")
    public ResponseEntity<Response> getAll(){
        return featureService.getAll();
    }
}
