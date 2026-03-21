package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.Category;
import com.khorunaliyev.kettu.entity.resources.Tag;
import com.khorunaliyev.kettu.repository.resource.CategoryRepository;
import com.khorunaliyev.kettu.repository.resource.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;


    public ResponseEntity<Response> createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
        return new ResponseEntity<>(new Response("New tag added", null ), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> getAllTags() {
        return new ResponseEntity<>(new Response("All tags fetched", tagRepository.findAll()), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> updateTag(Integer id, String newName) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag topilmadi: ID " + id));
        tag.setName(newName);
        tagRepository.save(tag);
        return ResponseEntity.ok(new Response("Success", tag));
    }
}
