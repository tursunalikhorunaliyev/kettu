package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import com.khorunaliyev.kettu.entity.resources.SubCategory;
import com.khorunaliyev.kettu.repository.resource.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService {
    private final SubCategoryRepository categoryRepository;
    private final MessageSource messageSource;


    @Cacheable(value = "categories", key = "T(org.springframework.context.i18n.LocaleContextHolder).getLocale().toLanguageTag()")
    public ResponseEntity<Response> getAll() {
        List<IDNameItemCountDTO> dtoList = categoryRepository.findAll().stream().map(category -> new IDNameItemCountDTO(category.getId(), messageSource.getMessage(category.getName(), null, LocaleContextHolder.getLocale()), category.getActiveItemCount())).toList();
        return ResponseEntity.ok(new Response("All categories", dtoList));
    }

    public ResponseEntity<Response> one(Integer categoryId) {
        SubCategory category = categoryRepository.findWithTagsById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return ResponseEntity.ok(new Response("Success", category));
    }

    public ResponseEntity<Response> createCategory(String name) {
        SubCategory category = new SubCategory();
        category.setName(name);
        categoryRepository.save(category);
        return new ResponseEntity<>(new Response("Category created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> updateCategoryName(Integer categoryId, String name) {
        SubCategory category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(name);
        categoryRepository.save(category);
        return ResponseEntity.ok(new Response("Category updated", null));
    }

    public ResponseEntity<Response> importFromExcel(MultipartFile file) {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<SubCategory> categories = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String name = row.getCell(0).getStringCellValue();
                if (name != null) {
                    SubCategory category = new SubCategory();
                    category.setName(name);
                    categories.add(category);
                }
            }
            categoryRepository.saveAll(categories);
            return new ResponseEntity<>(new Response("Successfully imported", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed, something went wrong", null), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<Response> assignTags(List<Integer> tags, Integer categoryId) {
        validateCategory(categoryId);

        Integer[] tagsArray = tags.toArray(new Integer[0]);

        categoryRepository.assignTagsBatch(categoryId, tagsArray);

        return new ResponseEntity<>(new Response("Success", "Tags assigned"), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Response> unassignTags(List<Integer> tags, Integer categoryId) {
        validateCategory(categoryId);

        Integer[] tagsArray = tags.toArray(new Integer[0]);

        categoryRepository.unassignTagsBatch(categoryId, tagsArray);

        return ResponseEntity.ok(new Response("Success", "Tags unassigned"));
    }

    private void validateCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Kategoriya topilmadi: ID " + categoryId);
        }
    }
}
