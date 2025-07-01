package com.khorunaliyev.kettu.services.place;

import com.khorunaliyev.kettu.dto.reponse.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {
    public ResponseEntity<Response> createPlace(String name, String description, List<String> photos){
    }
}
