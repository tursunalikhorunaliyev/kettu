package com.khorunaliyev.kettu.controller;

import com.khorunaliyev.kettu.services.r2service.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class FileController {

    private final R2Service r2Service;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(r2Service.upload(file));
    }

    @GetMapping("/{key}")
    public ResponseEntity<String> getFileUrl(@PathVariable String key){
        return ResponseEntity.ok(r2Service.getFileUrl(key));
    }
}
