package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.Tag;
import com.khorunaliyev.kettu.repository.resource.SubCategoryRepository;
import com.khorunaliyev.kettu.repository.resource.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagService {

    private final SubCategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Cacheable(value = "tags", key = "#subcategory")
    public ResponseEntity<Response> getAllTags(String subcategory) {
        return new ResponseEntity<>(new Response("All tags fetched", tagRepository.findAllBySubCategoryNameNative(subcategory)), HttpStatus.OK);
    }


    public ResponseEntity<Response> createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
        return new ResponseEntity<>(new Response("New tag added", null ), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Response> updateTag(Integer id, String newName) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag topilmadi: ID " + id));
        tag.setName(newName);
        tagRepository.save(tag);
        return ResponseEntity.ok(new Response("Success", tag));
    }
}
