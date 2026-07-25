package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.SubCategory;
import com.khorunaliyev.kettu.repository.resource.CategoryRepository;
import com.khorunaliyev.kettu.repository.resource.SubCategoryRepository;
import com.khorunaliyev.kettu.dto.projection.SubcategoryDetailInfo;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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


    @Cacheable(
            value = "subcategories",
            key = "T(org.springframework.context.i18n.LocaleContextHolder).getLocale().toLanguageTag() + '_' + #category"
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
    @CacheEvict(value = "tags", key = "#subcategorySlug")
    public ResponseEntity<Response> assignTags(List<Integer> tags, Integer subcategoryId, String subcategorySlug) {
        validateSubCategoryById(subcategoryId);

        Integer[] tagsArray = tags.toArray(new Integer[0]);

        subCategoryRepository.assignTagsBatch(subcategoryId, tagsArray);

        return new ResponseEntity<>(new Response("Success", "Tags assigned"), HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(value = "tags", key = "#subcategorySlug")
    public ResponseEntity<Response> unassignTags(List<Integer> tags, Integer subcategoryId, String subcategorySlug) {
        validateSubCategoryById(subcategoryId);

        Integer[] tagsArray = tags.toArray(new Integer[0]);

        subCategoryRepository.unassignTagsBatch(subcategoryId, tagsArray);

        return ResponseEntity.ok(new Response("Success", "Tags unassigned"));
    }

    @Cacheable(value = "tags", key = "#slug")
    public ResponseEntity<Response> tags(String slug) {
        return new ResponseEntity<>(new Response("Success", subCategoryRepository.findTagsBySubCategorySlug(slug)), HttpStatus.OK);
    }

    private void validateSubCategoryById(Integer subcategoryId) {
        if (!subCategoryRepository.existsById(subcategoryId)) {
            throw new ResourceNotFoundException("Not found");
        }
    }

    private void validateSubcategoryBySlug(String slug) {
        if(!subCategoryRepository.existsByName(slug)){
            throw new ResourceNotFoundException("Not found");
        }
    }

}
