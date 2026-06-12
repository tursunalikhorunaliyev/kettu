package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.category.CategoryNameRequest;
import com.khorunaliyev.kettu.dto.request.category.CategoryTagsRequest;
import com.khorunaliyev.kettu.services.resource.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/resources/category")
@RequiredArgsConstructor
public class CategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping("/")
    public ResponseEntity<Response> getAll(){
        return subCategoryService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Integer categoryId){
        return subCategoryService.one(categoryId);
    }

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid CategoryNameRequest request){
        return subCategoryService.createCategory(request.getName());
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("file") MultipartFile file){
        return subCategoryService.importFromExcel(file);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Response> updateCategoryName(@PathVariable("id") Integer categoryId, @RequestBody @Valid CategoryNameRequest request){
        return  subCategoryService.updateCategoryName(categoryId, request.getName());
    }

    @PostMapping("/assign-tags")
    public ResponseEntity<Response> assignTags(@RequestBody @Valid CategoryTagsRequest tagsRequest){
        return subCategoryService.assignTags(tagsRequest.getTag_ids(), tagsRequest.getCategory_id());
    }

    @DeleteMapping("/unassign-tags")
    public ResponseEntity<Response> unassignTags(@RequestBody @Valid CategoryTagsRequest tagsRequest){
        return subCategoryService.unassignTags(tagsRequest.getTag_ids(), tagsRequest.getCategory_id());
    }
}
