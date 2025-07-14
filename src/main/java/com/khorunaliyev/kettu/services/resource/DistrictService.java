package com.khorunaliyev.kettu.services.resource;

import com.khorunaliyev.kettu.config.adviser.ResourceNotFoundException;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.reponse.resource.IDNameItemCountDTO;
import com.khorunaliyev.kettu.entity.resources.District;
import com.khorunaliyev.kettu.entity.resources.Region;
import com.khorunaliyev.kettu.repository.resource.DistrictRepository;
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
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;

    public ResponseEntity<Response> createCity(Long regionId, String name){
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new ResourceNotFoundException("Region not found"));
        District district = new District();
        district.setName(name);
        district.setRegion(region);
        districtRepository.save(district);
        return new ResponseEntity<>(new Response("District created", null), HttpStatus.CREATED);
    }

    public ResponseEntity<Response> importFromExcel(Long regionId, MultipartFile file){
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new ResourceNotFoundException("Region not found"));
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<District> districts = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                String name = row.getCell(0).getStringCellValue();
                if (name != null) {
                    District district = new District();
                    district.setName(name);
                    district.setRegion(region);
                    districts.add(district);
                }
            }
            districtRepository.saveAll(districts);
            return new ResponseEntity<>(new Response("Successfully imported", null), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(new Response("Failed, something went wrong", null), HttpStatus.BAD_REQUEST);
        }
    }
    public ResponseEntity<Response> updateCityName(Long cityId, String name){
        District district = districtRepository.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("City not found"));
        district.setName(name);
        districtRepository.save(district);
        return ResponseEntity.ok(new Response("District updated", null));
    }

    public ResponseEntity<Response> getByRegion(Long regionId){
        return ResponseEntity.ok(new Response("Success",districtRepository.findByRegion_Id(regionId).stream().map(districtInfo -> new IDNameItemCountDTO(districtInfo.getId(), districtInfo.getName(), districtInfo.getActiveItemCount()))));

    }
}
