package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import com.khorunaliyev.kettu.entity.resources.Country;
import com.khorunaliyev.kettu.entity.resources.Region;
import com.khorunaliyev.kettu.repository.resource.CountryRepository;
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
public class RegionService {

    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;

    public ResponseEntity<Response> getByCountry(Long countryId){
        return ResponseEntity.ok(new Response("Success", regionRepository.findByCountryId(countryId).stream().map(regionInfo -> new IDNameItemCountDTO(regionInfo.getId(), regionInfo.getName(), regionInfo.getActiveItemCount()))));
    }

    public ResponseEntity<Response> createRegion(Long countryId, String name){
        Country country = countryRepository.findById(countryId).orElseThrow(() -> new ResourceNotFoundException("Country not found"));
        Region region = new Region();
        region.setName(name);
        region.setCountry(country);
        regionRepository.save(region);
        return new ResponseEntity<>(new Response("Region created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> updateRegionName(Long regionId, String name){
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new ResourceNotFoundException("Region not found"));
        region.setName(name);
        regionRepository.save(region);
        return ResponseEntity.ok(new Response("Region updated", null));
    }

    public ResponseEntity<Response> importFromExcel(Long countryId, MultipartFile file){
        Country country = countryRepository.findById(countryId).orElseThrow(() -> new ResourceNotFoundException("Country not found"));
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Region> regions = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String name = row.getCell(0).getStringCellValue();
                if (name != null) {
                    Region region = new Region();
                    region.setName(name);
                    region.setCountry(country);
                    regions.add(region);
                }
            }
            regionRepository.saveAll(regions);
            return new ResponseEntity<>(new Response("Successfully imported", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed, something went wrong", null), HttpStatus.BAD_REQUEST);
        }
    }
}
