package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.SubCategory;
import com.khorunaliyev.kettu.repository.resource.CategoryRepository;
import com.khorunaliyev.kettu.repository.resource.SubCategoryRepository;
import com.khorunaliyev.kettu.dto.projection.SubcategoryDetailInfo;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
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
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MessageSource messageSource;
    private final EntityManager entityManager;


    @Cacheable(
            value = "subcategories",
            key = "T(org.springframework.context.i18n.LocaleContextHolder).getLocale().toLanguageTag() + '_' + (#category ?: 'all')"
    )
    public ResponseEntity<Response> getAll(String category) {
        return ResponseEntity.ok(new Response("All categories", category == null ? subCategoryRepository.findAllBy() : subCategoryRepository.findByCategory_Name(category)));
    }


    @Cacheable(value = "subcategory-detail", key = "#subcategoryId")
    public ResponseEntity<Response> one(Integer subcategoryId) {
        SubcategoryDetailInfo category = subCategoryRepository.findWithTagsById(subcategoryId).orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
        return ResponseEntity.ok(new Response("Success", category));
    }

    @CacheEvict(value = "subcategories", allEntries = true)
    public ResponseEntity<Response> create(Integer categoryId, String name) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setCategory(categoryRepository.getReferenceById(categoryId));
        subCategoryRepository.save(subCategory);
        return new ResponseEntity<>(new Response("Category created", null), HttpStatus.CREATED);
    }


    @Caching(evict = {
            @CacheEvict(value = "subcategories", allEntries = true),
            @CacheEvict(value = "subcategory-detail", key = "#subcategoryId")
    })
    public ResponseEntity<Response> updateName(Integer subcategoryId, String name) {
        SubCategory category = subCategoryRepository.findById(subcategoryId).orElseThrow(() -> new ResourceNotFoundException("Subcategory not found"));
        category.setName(name);
        subCategoryRepository.save(category);
        return ResponseEntity.ok(new Response("Category updated", null));
    }

    @CacheEvict(value = "subcategories", allEntries = true)
    public ResponseEntity<Response> importFromExcel(Integer categoryId, MultipartFile file) {

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<SubCategory> categories = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String name = row.getCell(0).getStringCellValue();
                if (name != null) {
                    SubCategory category = new SubCategory();
                    category.setName(name);
                    category.setCategory(categoryRepository.getReferenceById(categoryId));
                    categories.add(category);
                }
            }
            subCategoryRepository.saveAll(categories);
            return new ResponseEntity<>(new Response("Successfully imported", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed, something went wrong", null), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    public ResponseEntity<Response> assignTags(List<Integer> tags, Integer subcategoryId) {
        validateCategory(subcategoryId);

        Integer[] tagsArray = tags.toArray(new Integer[0]);

        subCategoryRepository.assignTagsBatch(subcategoryId, tagsArray);

        return new ResponseEntity<>(new Response("Success", "Tags assigned"), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Response> unassignTags(List<Integer> tags, Integer categoryId) {
        validateCategory(categoryId);

        Integer[] tagsArray = tags.toArray(new Integer[0]);

        subCategoryRepository.unassignTagsBatch(categoryId, tagsArray);

        return ResponseEntity.ok(new Response("Success", "Tags unassigned"));
    }

    private void validateCategory(Integer categoryId) {
        if (!subCategoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Kategoriya topilmadi: ID " + categoryId);
        }
    }
}
