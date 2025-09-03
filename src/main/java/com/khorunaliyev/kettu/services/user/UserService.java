package com.khorunaliyev.kettu.services.user;

import com.khorunaliyev.kettu.config.security.JWTGenerator;
import com.khorunaliyev.kettu.dto.projection.PlaceInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import com.khorunaliyev.kettu.repository.place.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JWTGenerator jwtGenerator;
    private final PlaceRepository placeRepository;


    public ResponseEntity<Response> getMe(Authentication authentication){
        return ResponseEntity.ok(new Response("User data", userRepository.findDetachedByEmail(authentication.getName())));
    }

/*
    public ResponseEntity<Response> update(){

    }

    public ResponseEntity<Response> getPlaces(Authentication authentication){
        return placeRepository.findBy()
    }
*/


}
