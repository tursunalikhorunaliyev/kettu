package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.category.CategoryNameRequest;
import com.khorunaliyev.kettu.dto.request.category.CategoryTagsRequest;
import com.khorunaliyev.kettu.dto.request.category.NewCategoryRequest;
import com.khorunaliyev.kettu.services.resource.CategoryService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{id}")
    public ResponseEntity<Response> getOne(@PathVariable("id") Integer categoryId){
        return categoryService.one(categoryId);
    }

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid CategoryNameRequest request){
        return categoryService.createCategory(request.getName());
    }

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("file") MultipartFile file){
        return categoryService.importFromExcel(file);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Response> updateCategoryName(@PathVariable("id") Integer categoryId, @RequestBody @Valid CategoryNameRequest request){
        return  categoryService.updateCategoryName(categoryId, request.getName());
    }

    @PostMapping("/assign-tags")
    public ResponseEntity<Response> assignTags(@RequestBody @Valid CategoryTagsRequest tagsRequest){
        return categoryService.assignTags(tagsRequest.getTag_ids(), tagsRequest.getCategory_id());
    }

    @DeleteMapping("/unassign-tags")
    public ResponseEntity<Response> unassignTags(@RequestBody @Valid CategoryTagsRequest tagsRequest){
        return categoryService.unassignTags(tagsRequest.getTag_ids(), tagsRequest.getCategory_id());
    }
}
