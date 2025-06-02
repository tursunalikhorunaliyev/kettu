package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.entity.resources.Country;
import com.khorunaliyev.kettu.repository.resource.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/resources/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryRepository countryRepository;


    @GetMapping("/all")
    public ResponseEntity<List<Country>> getAll(){
        return ResponseEntity.ok(countryRepository.findAll());
    }

    @PostMapping("/save")
    public void save(@RequestParam(name = "name") String name){
        Country country = new Country();
        country.setName(name);
        countryRepository.save(country);
    }
}
