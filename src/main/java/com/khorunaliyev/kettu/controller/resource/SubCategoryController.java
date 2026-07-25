package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.category.CategoryNameRequest;
import com.khorunaliyev.kettu.dto.request.category.CategoryTagsRequest;
import com.khorunaliyev.kettu.dto.request.subcategory.NewSubCategoryRequest;
import com.khorunaliyev.kettu.services.resource.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/subcategories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping
    public ResponseEntity<Response> getAll(@RequestParam(value = "category") String category) {
        return subCategoryService.getAll(category);
    }

    @GetMapping("/{slug}/tags")
    public ResponseEntity<Response> tags(@PathVariable("slug") String slug) {
        return subCategoryService.tags(slug);
    }

    @PostMapping
    public ResponseEntity<Response> save(@RequestBody @Valid NewSubCategoryRequest request) {
        return subCategoryService.create(request.getCategory_id(), request.getName());
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("category_id") Integer categoryId, @RequestParam("file") MultipartFile file) {
        return subCategoryService.importFromExcel(categoryId, file);
    }

    @PostMapping("/assign-tags")
    public ResponseEntity<Response> assignTags(@RequestBody @Valid CategoryTagsRequest tagsRequest) {
        return subCategoryService.assignTags(tagsRequest.getTag_ids(), tagsRequest.getSub_category_id(), tagsRequest.getSub_category_slug().trim());
    }

    @PostMapping("/unassign-tags")
    public ResponseEntity<Response> unassignTags(@RequestBody @Valid CategoryTagsRequest tagsRequest) {
        return subCategoryService.unassignTags(tagsRequest.getTag_ids(), tagsRequest.getSub_category_id(), tagsRequest.getSub_category_slug().trim());
    }
}
