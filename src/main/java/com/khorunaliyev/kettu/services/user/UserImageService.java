package com.khorunaliyev.kettu.services.user;

import com.khorunaliyev.kettu.entity.auth.AppUser;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import com.khorunaliyev.kettu.services.storage.R2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImageService {
    private final UserRepository userRepository;
    private final R2Service r2Service;

    @Async("imageExecutor")
    public void processAndUpload(Long userId, String profilePhoto, String backgroundPhoto) {
        if (profilePhoto != null) {
            processSingleImage(userId, profilePhoto, "profile");
        }
        if (backgroundPhoto != null) {
            processSingleImage(userId, backgroundPhoto, "background");
        }
    }

    private void processSingleImage(Long userId, String path, String type) {
        File originalFile = new File(path);

        if (!originalFile.exists()) {
            log.error("Fayl topilmadi: {}", path);
            return;
        }

        String uuid = UUID.randomUUID().toString();
        try {
            compressAndUpload(originalFile, uuid, type);
            updateUserPhotoUrl(userId, type, uuid);
            log.info("User {} uchun {} rasm muvaffaqiyatli yuklandi. UUID: {}", userId, type, uuid);
        } catch (Exception io) {
            log.error("User {} uchun {} rasmini qayta ishlashda xato: {}", userId, type, io.getMessage());
        } finally {
            try {
                Files.deleteIfExists(originalFile.toPath());
            } catch (IOException e) {
                log.error("Vaqtinchalik faylni o'chirishda xato: {}", originalFile.getName());
            }
        }
    }

    private void compressAndUpload(File source, String uuid, String type) throws IOException {
        String fileName = type + "_" + uuid + ".jpg";
        File tempProcessed = new File(source.getParent(), "temp_" + fileName);
        String keyForR2 = "users/" + type + "s/" + uuid + ".jpg";

        try {
            Thumbnails.of(source)
                    .scale(0.8)
                    .outputQuality(0.7)
                    .outputFormat("jpg")
                    .toFile(tempProcessed);

            r2Service.upload(tempProcessed, keyForR2);
        } finally {
            Files.deleteIfExists(tempProcessed.toPath());
        }
    }

    @Transactional
    public void updateUserPhotoUrl(Long userId, String type, String uuid) {
        AppUser user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            String profilePhoto = user.getImage();
            String backgroundPhoto = user.getBackgroundImage();
            if (type.equals("profile")) user.setImage(uuid);
            else user.setBackgroundImage(uuid);
            userRepository.save(user);

            if(!profilePhoto.startsWith("https://")){
                r2Service.deleteFile("users/" + type + "s/" + profilePhoto + ".jpg");
            }
            if(backgroundPhoto!=null){
                r2Service.deleteFile("users/" + type + "s/" + backgroundPhoto + ".jpg");
            }
        }
    }
}
