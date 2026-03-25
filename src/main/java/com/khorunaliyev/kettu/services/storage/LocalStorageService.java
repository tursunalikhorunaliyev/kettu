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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageService {

    public List<String> saveToTempDirectory(String key, List<MultipartFile> files) throws IOException {
        Path baseTempFile = Paths.get(System.getProperty("java.io.tmpdir"), "kettu_uploads", key);

        if(!Files.exists(baseTempFile)){
            Files.createDirectories(baseTempFile);
        }

        List<String> fullPaths = new LinkedList<>();

        for (MultipartFile file: files){
            if(file.isEmpty()) continue;

            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileName = UUID.randomUUID() + "_" + originalFileName;


            Path targetPath = baseTempFile.resolve(fileName);

            file.transferTo(targetPath.toFile());

            fullPaths.add(targetPath.toAbsolutePath().toString());
            log.info("Fayl vaqtincha diskka saqlandi: {}", targetPath.getFileName());
        }

        return fullPaths;
    }
}
