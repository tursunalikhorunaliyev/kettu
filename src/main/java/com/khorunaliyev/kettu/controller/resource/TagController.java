package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.services.resource.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/resources/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/create")
    public ResponseEntity<Response> create(@RequestParam String name) {
        return tagService.createTag(name);
    }

    @GetMapping("/")
    public ResponseEntity<Response> getAll() {
        return tagService.getAllTags();
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Response> update(@PathVariable Integer id, @RequestParam String name) {
        return tagService.updateTag(id, name);
    }
}