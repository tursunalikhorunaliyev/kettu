package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.Category;
import com.khorunaliyev.kettu.entity.resources.SubCategory;
import com.khorunaliyev.kettu.repository.resource.CategoryRepository;
import com.khorunaliyev.kettu.repository.resource.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;


    public ResponseEntity<Response> createSubCategory(String name, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setCategory(category);
        subCategoryRepository.save(subCategory);
        return new ResponseEntity<>(new Response("SubCategory created", null), HttpStatus.CREATED);
    }


    public ResponseEntity<Response> updateSubCategory(Long subCategoryId, String name) {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(() -> new ResourceNotFoundException("SubCategory not found"));
        subCategory.setName(name);
        subCategoryRepository.save(subCategory);
        return ResponseEntity.ok(new Response("SubCategory updated", null));
    }


    public ResponseEntity<Response> getSubCategoriesByCategoryId(Long categoryId) {
        List<SubCategory> subCategories = subCategoryRepository.findByCategory_Id(categoryId);
        return ResponseEntity.ok(new Response("SubCategories", subCategories));
    }

    public ResponseEntity<Response> importFromExcel(Long categoryId, MultipartFile file) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<SubCategory> subCategories = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = row.getCell(0).getStringCellValue();

                if (name != null && !name.trim().isEmpty()) {
                    SubCategory subCategory = new SubCategory();
                    subCategory.setName(name.trim());
                    subCategory.setCategory(category);
                    subCategories.add(subCategory);
                }
            }

            subCategoryRepository.saveAll(subCategories);
            return new ResponseEntity<>(new Response("Successfully imported subcategories", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed to import", null), HttpStatus.BAD_REQUEST);
        }
    }
}
