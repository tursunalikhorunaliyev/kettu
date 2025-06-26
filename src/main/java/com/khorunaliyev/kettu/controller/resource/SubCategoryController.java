package com.khorunaliyev.kettu.controller.resource;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.subcategory.NewSubCategoryRequest;
import com.khorunaliyev.kettu.dto.request.subcategory.SubCategoryNameRequest;
import com.khorunaliyev.kettu.services.resource.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/sub-category")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping
    public ResponseEntity<Response> getByCategoryId(@RequestParam Long categoryId) {
        return subCategoryService.getSubCategoriesByCategoryId(categoryId);
    }

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid NewSubCategoryRequest request) {
        return subCategoryService.createSubCategory(request.getName(), request.getCategoryId());
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("category_id") Long categoryId, @RequestParam("file") MultipartFile file) {
        return subCategoryService.importFromExcel(categoryId, file);
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> update(@PathVariable("id") Long subCategoryId, @RequestBody @Valid SubCategoryNameRequest request) {
        return subCategoryService.updateSubCategory(subCategoryId, request.getName());
    }
}
