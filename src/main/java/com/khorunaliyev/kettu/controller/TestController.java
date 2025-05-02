package com.khorunaliyev.kettu.controller;

import com.khorunaliyev.kettu.entity.Student;
import com.khorunaliyev.kettu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestController {

    private final StudentRepository studentRepository;


    @GetMapping("/")
    public Principal home(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("name ", principal.getName());
        }
        return principal;
    }

    @GetMapping("/test")
    public ResponseEntity<Student> test(){
        return ResponseEntity.ok(studentRepository.findByName("Tursunali").get());
    }


}
