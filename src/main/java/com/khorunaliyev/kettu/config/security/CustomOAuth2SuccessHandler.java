package com.khorunaliyev.kettu.config.security;

import com.khorunaliyev.kettu.entity.User;
import com.khorunaliyev.kettu.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTGenerator jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("picture");


        // Save or update user
        User user = userRepository.findByEmail(email).orElseGet(() -> new User());
        user.setEmail(email);
        user.setName(name);
        userRepository.save(user);

        // Generate JWT
        String jwtToken = jwtService.generateToken(user);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");

        // Redirect to Flutter app via deep link with token
        //response.sendRedirect("http://localhost:8080/user?token=" + jwtToken);
    }
}
