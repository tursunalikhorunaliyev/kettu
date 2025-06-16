package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.CategoryNameRequest;
import com.khorunaliyev.kettu.dto.request.NewCategoryRequest;
import com.khorunaliyev.kettu.services.resource.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<Response> getAll(){
        return categoryService.getAll();
    }

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid NewCategoryRequest request){
        return categoryService.createCategory(request.getName(), request.getFeatureId());
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("feature_id") Long featureId, @RequestParam("file") MultipartFile file){
        return categoryService.importFromExcel(featureId, file);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> updateCategoryName(@PathVariable("id") Long categoryId, @RequestBody @Valid CategoryNameRequest request){
        return  categoryService.updateCategoryName(categoryId, request.getName());
    }
}
