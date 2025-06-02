package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.entity.resources.Country;
import com.khorunaliyev.kettu.entity.resources.Region;
import com.khorunaliyev.kettu.repository.resource.CountryRepository;
import com.khorunaliyev.kettu.repository.resource.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/resources/region")
@RequiredArgsConstructor
public class RegionController {

    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;

    @GetMapping("/")
    public ResponseEntity<Region> getRegion(@RequestParam Long id){
        return ResponseEntity.ok(regionRepository.findById(id).orElse(null));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Region>> getAll(){
        return ResponseEntity.ok(regionRepository.findAll());
    }



    @PostMapping("/save")
    public void save(@RequestParam(name = "country_id") Long id, @RequestParam(name = "region_name") String name){
        Optional<Country> optionalCountry = countryRepository.findById(id);
        if(optionalCountry.isPresent()){
            Region region = new Region();
            region.setName(name);
            optionalCountry.get().getRegions().add(region);
            countryRepository.save(optionalCountry.get());
        }
    }

}
