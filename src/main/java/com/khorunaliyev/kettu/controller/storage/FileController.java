package com.khorunaliyev.kettu.controller.storage;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.services.r2service.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class FileController {

    private final R2Service r2Service;

    @PostMapping("/upload-single")
    public ResponseEntity<Response> upload(@RequestParam("file") MultipartFile image) throws IOException {
        return r2Service.upload(image);
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<Response> uploadMultiple(@RequestParam("files") List<MultipartFile> images){
        return r2Service.uploadMultiple(images);
    }

    @GetMapping("/{key}")
    public ResponseEntity<Response> getFileUrl(@PathVariable String key){
        return r2Service.getFileUrl(key);
    }
    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {

        byte[] imageData = r2Service.downloadFile(filename); // returns byte[]
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG); // or IMAGE_PNG, etc.
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
    }
}
