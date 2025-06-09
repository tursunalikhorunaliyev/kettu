package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.Country;
import com.khorunaliyev.kettu.repository.resource.CountryRepository;
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
public class CountryService {
    private final CountryRepository countryRepository;

    public ResponseEntity<Response> createCountry(String name) {
        Country newCountry = new Country();
        newCountry.setName(name);
        countryRepository.save(newCountry);
        return new ResponseEntity<>(new Response("Country created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> all() {
        return ResponseEntity.ok(new Response("All countries", countryRepository.findAllBy()));
    }

    public ResponseEntity<Response> updateCountryName(Long id, String name) {
        Country country = countryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Country not found"));
        country.setName(name);
        countryRepository.save(country);
        return new ResponseEntity<>(new Response("Country updated", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> importFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Country> countries = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String name = row.getCell(0).getStringCellValue();
                if (name != null) {
                    Country country = new Country();
                    country.setName(name);
                    countries.add(country);
                }
            }
            countryRepository.saveAll(countries);
            return new ResponseEntity<>(new Response("Successfully imported", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed, something went wrong", null), HttpStatus.BAD_REQUEST);
        }

    }
}
