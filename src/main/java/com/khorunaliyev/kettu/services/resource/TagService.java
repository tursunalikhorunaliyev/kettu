package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.Category;
import com.khorunaliyev.kettu.entity.resources.Tag;
import com.khorunaliyev.kettu.repository.resource.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final CategoryRepository categoryRepository;


    public ResponseEntity<Response> createTag(String name, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Tag tag = new Tag();
        tag.setName(name);
        category.getTags().add(tag);
        categoryRepository.save(category);
        return new ResponseEntity<>(new Response("New tag added", null ), HttpStatus.CREATED);
    }
}
