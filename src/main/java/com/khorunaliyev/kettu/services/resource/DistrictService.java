package com.khorunaliyev.kettu.services.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.wololo.jts2geojson.GeoJSONReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictService {

    private final DistrictRepository districtRepository;
    private final RegionRepository regionRepository;
    private final ObjectMapper objectMapper;

    public ResponseEntity<Response> createCity(Long regionId, String name) {
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new ResourceNotFoundException("Region not found"));
        District district = new District();
        district.setName(name);
        district.setRegion(region);
        districtRepository.save(district);
        return new ResponseEntity<>(new Response("District created", null), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Response> importFromGeoJson(Long regionId, MultipartFile file) throws IOException {
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new ResourceNotFoundException("Region not found"));
        JsonNode rootNode = objectMapper.readTree(file.getInputStream());
        JsonNode features = rootNode.get("features");
        GeoJSONReader reader = new GeoJSONReader();

        System.out.println(rootNode);
        System.out.println("----------------------------------------------------------");
        System.out.println(features);

        List<District> districts = new LinkedList<>();
        if (features != null && features.isArray() && !features.isEmpty()) {
            for (JsonNode feature : features) {
                System.out.println(feature);
                System.out.println("fffffffffffffffffffffffffffff");

                ///nomni ajratib olish
                JsonNode properties = feature.get("properties");

                System.out.println(properties);

                String districtName = properties.get("ADM1_UZ").asText();


                ///poligon kordinatalarini o'qish
                String geoJsonString = feature.get("geometry").toString();
                Geometry geometry = reader.read(geoJsonString);
                District district = new District();
                district.setRegion(region);
                district.setName(districtName);

                if(geometry instanceof Polygon){
                    district.setGeom(geometry.getFactory().createMultiPolygon(new Polygon[]{(Polygon) geometry}));
                }
                else{
                    district.setGeom((MultiPolygon) geometry);
                }
                districts.add(district);
            }

            districtRepository.saveAll(districts);
        }

        return ResponseEntity.ok(new Response("Success", "Districts created"));
    }

    public ResponseEntity<Response> updateCityName(Long cityId, String name) {
        District district = districtRepository.findById(cityId).orElseThrow(() -> new ResourceNotFoundException("City not found"));
        district.setName(name);
        districtRepository.save(district);
        return ResponseEntity.ok(new Response("District updated", null));
    }

    public ResponseEntity<Response> getByRegion(Long regionId) {
        return ResponseEntity.ok(new Response("Success", districtRepository.findByRegion_Id(regionId).stream().map(districtInfo -> new IDNameItemCountDTO(districtInfo.getId(), districtInfo.getName(), districtInfo.getActiveItemCount()))));

    }
}
