package com.khorunaliyev.kettu.controller.resource;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.CountryNameRequest;
import com.khorunaliyev.kettu.services.resource.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


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

    @PostMapping("/import")
    public ResponseEntity<Response> importFromExcel(@RequestParam("file") MultipartFile file){
       return countryService.importFromExcel(file);
    }

    @GetMapping("/")
    public ResponseEntity<Response> save(){
      return countryService.getAll();
    }
}
