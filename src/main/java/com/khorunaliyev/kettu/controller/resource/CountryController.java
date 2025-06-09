package com.khorunaliyev.kettu.controller.resource;

import com.khorunaliyev.kettu.dto.projection.CountryInfo;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.CountryNameRequest;
import com.khorunaliyev.kettu.entity.resources.Country;
import com.khorunaliyev.kettu.repository.resource.CountryRepository;
import com.khorunaliyev.kettu.services.resource.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/resources/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;


    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody @Valid CountryNameRequest countryNameRequest){
      return   countryService.createCountry(countryNameRequest.getName());
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<Response> updateCountryName(@PathVariable("id") Long countryId, @RequestBody() @Valid CountryNameRequest request){
      return countryService.updateCountryName(countryId,request.getName());
    }

    @PostMapping("/upload")
    public ResponseEntity<Response> uploadFromExcel(@RequestParam("file") MultipartFile file){
       return countryService.importFromExcel(file);
    }

    @GetMapping("/")
    public ResponseEntity<Response> save(){
      return countryService.all();
    }
}
