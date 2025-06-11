package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.entity.resources.City;
import com.khorunaliyev.kettu.entity.resources.Region;
import com.khorunaliyev.kettu.repository.resource.CityRepository;
import com.khorunaliyev.kettu.repository.resource.RegionRepository;
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
public class CityService {

    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;

    public ResponseEntity<Response> createCity(Long regionId, String name){
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new ResourceNotFoundException("Region not found"));
        City city = new City();
        city.setName(name);
        city.setRegion(region);
        cityRepository.save(city);
        return new ResponseEntity<>(new Response("City created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> importFromExcel(Long regionId, MultipartFile file){
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new ResourceNotFoundException("Region not found"));
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<City> cities = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String name = row.getCell(0).getStringCellValue();
                if (name != null) {
                    City city = new City();
                    city.setName(name);
                    city.setRegion(region);
                    cities.add(city);
                }
            }
            cityRepository.saveAll(cities);
            return new ResponseEntity<>(new Response("Successfully imported", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed, something went wrong", null), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<Response> updateCityName(Long cityId, String name){
        City city = cityRepository.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("City not found"));
        city.setName(name);
        cityRepository.save(city);
        return ResponseEntity.ok(new Response("City updated", null));
    }

    public ResponseEntity<Response> getByRegion(Long regionId){
        return ResponseEntity.ok(new Response("Success",cityRepository.findByRegion_Id(regionId)));

    }
}
