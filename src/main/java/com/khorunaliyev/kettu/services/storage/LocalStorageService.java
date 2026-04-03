package com.khorunaliyev.kettu.services.storage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageService {

    public Map<String, List<String>> saveToTempDirectory(String key, MultipartFile mainPhoto, List<MultipartFile> additionalPhotos) throws IOException {
        Path baseTempFile = Paths.get(System.getProperty("java.io.tmpdir"), "kettu_uploads", key);

        if(!Files.exists(baseTempFile)){
            Files.createDirectories(baseTempFile);
        }

        Map<String, List<String>> map = new LinkedHashMap<>();
        List<String> mainPhotoPath = new LinkedList<>();
        mainPhotoPath.add(processOne(mainPhoto, baseTempFile));

        List<String> fullPaths = new LinkedList<>();

        for (MultipartFile photo: additionalPhotos){
            if(photo.isEmpty()) continue;
            fullPaths.add(processOne(photo, baseTempFile));
            log.info("Fayl vaqtincha diskka saqlandi: {}", fullPaths);
        }

        map.put("main", mainPhotoPath);
        map.put("additional", fullPaths);

        return map;
    }

    private String processOne(MultipartFile photo, Path baseTempDirectory) throws IOException {
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(photo.getOriginalFilename()));
        String fileName = UUID.randomUUID() + "_" + originalFileName;
        Path targetPath = baseTempDirectory.resolve(fileName);
        photo.transferTo(targetPath.toFile());
        return targetPath.toAbsolutePath().toString();
    }

    public String saveToTempOne(String key, MultipartFile photo) throws IOException {
        Path baseTempFile = Paths.get(System.getProperty("java.io.tmpdir"), "kettu_uploads", key);
        if(!Files.exists(baseTempFile)){
            Files.createDirectories(baseTempFile);
        }
        return processOne(photo, baseTempFile);
    }
}