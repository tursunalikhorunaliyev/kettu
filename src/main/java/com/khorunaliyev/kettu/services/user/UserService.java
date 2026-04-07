package com.khorunaliyev.kettu.services.user;

import com.khorunaliyev.kettu.component.UserContext;
import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.user.UserUpdate;
import com.khorunaliyev.kettu.entity.auth.AppUser;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import com.khorunaliyev.kettu.services.storage.LocalStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserContext userContext;
    private final LocalStorageService localStorageService;
    private final UserImageService userImageService;


    public ResponseEntity<Response> getMe(Authentication authentication) {
        return ResponseEntity.ok(new Response("User data", userRepository.findDetachedByEmail(authentication.getName())));
    }

    public ResponseEntity<Response> update(UserUpdate userUpdate, MultipartFile profilePhoto, MultipartFile backgroundPhoto) throws IOException {
        Long userId = userContext.getUserId();
        final AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if(userUpdate!=null){
            System.out.println("---------");
            if (userUpdate.getName() != null) {
                appUser.setName(userUpdate.getName().trim());
            }
            if (userUpdate.getBio() != null) {
                appUser.setBio(userUpdate.getBio());
            }
            userRepository.save(appUser);
        }

        String profilePhotoPath = (profilePhoto != null && !profilePhoto.isEmpty())
                ? localStorageService.saveToTempOne(appUser.getEmail(), profilePhoto)
                : null;

        String backgroundPhotoPath = (backgroundPhoto != null && !backgroundPhoto.isEmpty())
                ? localStorageService.saveToTempOne(appUser.getEmail(), backgroundPhoto)
                : null;

        userImageService.processAndUpload(userId, profilePhotoPath, backgroundPhotoPath);

        return ResponseEntity.ok(new Response("User updated", appUser));
    }




}
