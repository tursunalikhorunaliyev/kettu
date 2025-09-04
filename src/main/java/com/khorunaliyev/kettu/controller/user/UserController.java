package com.khorunaliyev.kettu.controller.user;

import com.khorunaliyev.kettu.dto.reponse.Response;
import com.khorunaliyev.kettu.dto.request.user.UserUpdate;
import com.khorunaliyev.kettu.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<Response> me(Authentication authentication) {
        return userService.getMe(authentication);
    }

    @PatchMapping("/update")
    public ResponseEntity<Response> update(Authentication authentication, @RequestBody @Valid UserUpdate userUpdate){
        return userService.update(authentication,userUpdate);
    }
}
