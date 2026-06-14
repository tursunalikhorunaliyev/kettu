package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.projection.CategoryInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.Category;
import com.khorunaliyev.kettu.repository.resource.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Cacheable(value = "categories")
    public ResponseEntity<Response> all(){
        return ResponseEntity.ok(new Response("Success", categoryRepository.findAllBy()));
    }

    @CacheEvict(value = "categories", allEntries = true)
    public ResponseEntity<Response> create(String name){
        Category category = new Category();
        category.setName(name.trim());
        categoryRepository.save(category);
        return new ResponseEntity<>(new Response("Success", "Category created"), HttpStatus.CREATED);
    }

    @CacheEvict(value = "categories", allEntries = true)
    public ResponseEntity<Response> update(Integer id,String name){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(name);
        categoryRepository.save(category);
        return ResponseEntity.ok(new Response("Success", "Category updated"));
    }
}
