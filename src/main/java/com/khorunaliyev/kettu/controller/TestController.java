package com.khorunaliyev.kettu.controller;

import com.khorunaliyev.kettu.entity.User;
import com.khorunaliyev.kettu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("api/public")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;


    @GetMapping("/")
    public Principal home(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("name ", principal.getName());
        }
        return principal;
    }

    @GetMapping("/bool")
    public ResponseEntity<Boolean> getBool(){
        return ResponseEntity.ok(true);
    }

    @GetMapping("/test")
    public ResponseEntity<User> getAdmin(){
        return ResponseEntity.ok(userRepository.findByEmail("khorunaliyev@gmail.com").get());
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello");
    }

}
