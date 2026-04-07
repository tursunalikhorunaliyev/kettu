package com.khorunaliyev.kettu.controller.user;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.user.UserUpdate;
import com.khorunaliyev.kettu.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<Response> me(Authentication authentication) {
        return userService.getMe(authentication);
    }


    @PatchMapping(value = "/update",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> update(@RequestPart(value = "data", required = false) @Valid UserUpdate userUpdate, @RequestPart(value = "profile_photo", required = false) MultipartFile profilePhoto, @RequestPart(value = "background_photo", required = false) MultipartFile backgroundPhoto) throws IOException {
        return userService.update(userUpdate, profilePhoto, backgroundPhoto);
    }
}
