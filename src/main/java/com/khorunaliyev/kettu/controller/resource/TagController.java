package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.services.resource.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/resources/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/by-sub")
    public ResponseEntity<Response> bySubcategory(@RequestParam(name = "sub") String subcategory) {
        return tagService.bySubcategory(subcategory);
    }

    @GetMapping
    public ResponseEntity<Response> getAll(@RequestParam(defaultValue = "0") int page){
        return tagService.getAll(page);
    }

    @PostMapping
    public ResponseEntity<Response> create(@RequestParam String name) {
        return tagService.createTag(name);
    }
}