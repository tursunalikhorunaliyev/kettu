package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import com.khorunaliyev.kettu.entity.resources.Category;
import com.khorunaliyev.kettu.entity.resources.Country;
import com.khorunaliyev.kettu.entity.resources.Feature;
import com.khorunaliyev.kettu.repository.resource.CategoryRepository;
import com.khorunaliyev.kettu.repository.resource.FeatureRepository;
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
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final FeatureRepository featureRepository;


    public ResponseEntity<Response> createCategory(String name, Long featureId){
        Feature feature = featureRepository.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature not found"));
        Category category = new Category();
        category.setName(name);
        category.setFeature(feature);
        categoryRepository.save(category);
        return new ResponseEntity<>(new Response("Category created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> updateCategoryName(Long categoryId, String name){
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(name);
        categoryRepository.save(category);
        return ResponseEntity.ok(new Response("Category updated", null));
    }

    public ResponseEntity<Response> importFromExcel(Long featureId, MultipartFile file){
        Feature feature = featureRepository.findById(featureId).orElseThrow(() -> new ResourceNotFoundException("Feature not found"));
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Category> categories = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String name = row.getCell(0).getStringCellValue();
                if (name != null) {
                    Category category = new Category();
                    category.setName(name);
                    category.setFeature(feature);
                    categories.add(category);
                }
            }
            categoryRepository.saveAll(categories);
            return new ResponseEntity<>(new Response("Successfully imported", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed, something went wrong", null), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Response> getAll(){
        return ResponseEntity.ok(new Response("Categories", categoryRepository.findAllBy().stream().map(categoryInfo -> new IDNameItemCountDTO(categoryInfo.getId(), categoryInfo.getName(), categoryInfo.getActiveItemCount()))));
    }
}
