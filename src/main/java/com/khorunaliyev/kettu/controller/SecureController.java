package com.khorunaliyev.kettu.controller;

import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/secure")
@RequiredArgsConstructor
public class SecureController {

    private final UserRepository userRepository;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<User> getAdmin(){
        return ResponseEntity.ok(userRepository.findByEmail("khorunaliyev@gmail.com").get());
    }
}
