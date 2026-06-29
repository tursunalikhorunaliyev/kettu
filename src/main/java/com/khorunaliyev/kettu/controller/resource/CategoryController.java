package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.category.CategoryNameRequest;
import com.khorunaliyev.kettu.services.resource.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Response> all() {
        return categoryService.all();
    }

    @PostMapping
    public ResponseEntity<Response> save(CategoryNameRequest request) {
        return categoryService.create(request.getName());
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("file") MultipartFile file){
       return categoryService.importFromExcel(file);
    }
}
