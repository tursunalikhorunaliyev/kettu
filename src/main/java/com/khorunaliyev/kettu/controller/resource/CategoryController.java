package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.category.CategoryNameRequest;
import com.khorunaliyev.kettu.services.resource.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/resources/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Response> all() {
        return categoryService.all();
    }

    @PostMapping("/new")
    public ResponseEntity<Response> save(CategoryNameRequest request) {
        return categoryService.create(request.getName());
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Response> update(@PathVariable("id") Integer categoryId, @RequestBody @Valid CategoryNameRequest request) {
        return categoryService.update(categoryId, request.getName());
    }
}
