package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.NearbyThings;
import com.khorunaliyev.kettu.repository.resource.NearbyThingsRepository;
import com.khorunaliyev.kettu.services.r2service.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NearbyThingsService {
    private final NearbyThingsRepository nearbyThingsRepository;
    private final R2Service r2Service;

    public ResponseEntity<Response> createThing(String name, MultipartFile file) {
        NearbyThings nearbyThings = new NearbyThings();
        try {
            ResponseEntity<Response> icon = r2Service.upload(file);
            System.out.println(icon);
            nearbyThings.setName(name);
            nearbyThings.setIcon((String) Objects.requireNonNull(icon.getBody()).data());
            nearbyThingsRepository.save(nearbyThings);
            return new ResponseEntity<>(new Response("Nearby things created", null), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new Response("Something went wrong. Try again later", null), HttpStatus.BAD_GATEWAY);
        }
    }

    public ResponseEntity<Response> updateName(Long id, String name){
        NearbyThings nearbyThings = nearbyThingsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nearby things not found"));
        nearbyThings.setName(name);
        nearbyThingsRepository.save(nearbyThings);
        return ResponseEntity.ok(new Response("Nearby things has updated", null));
    }

    public ResponseEntity<Response> updateIcon(Long id, MultipartFile newIcon){
        NearbyThings nearbyThings = nearbyThingsRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Nearby things not found"));
        try {
            String oldIcon = nearbyThings.getIcon();
            ResponseEntity<Response> icon = r2Service.upload(newIcon);
            nearbyThings.setIcon((String) Objects.requireNonNull(icon.getBody()).data());
            nearbyThingsRepository.save(nearbyThings);
            r2Service.deleteFile(oldIcon);
            return ResponseEntity.ok(new Response("Nearby things has updated", null));
        }
        catch (Exception io){
            return new ResponseEntity<>(new Response("Something went wrong. Try again later", null), HttpStatus.BAD_GATEWAY);
        }
    }

    public ResponseEntity<Response> getAll(){
        return ResponseEntity.ok(new Response("Nearby things", nearbyThingsRepository.findAllBy()));
    }
}
