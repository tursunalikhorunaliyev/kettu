package com.khorunaliyev.kettu.controller.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class AuthController {

    @RequestMapping("api/auth/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @RequestMapping("api/auth/login/redirect")
    public ResponseEntity<String> loginRedirect(HttpServletResponse response){
        return ResponseEntity.ok("You successfully logged in");
    }
}
