package com.khorunaliyev.kettu.config.security;

import com.khorunaliyev.kettu.entity.auth.Role;
import com.khorunaliyev.kettu.entity.auth.User;
import com.khorunaliyev.kettu.repository.auth.RoleRepository;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTGenerator jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String image = oauthUser.getAttribute("picture");


        // Save or update user
        User user = userRepository.findByEmail(email).orElseGet(User::new);

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("USER");
                    return roleRepository.save(role);
                });
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ADMIN");
                    return roleRepository.save(role);
                });

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);

        assert email != null;
        if(email.equals("khorunaliyev@gmail.com")){
            userRoles.add(adminRole);
        }

        user.setEmail(email);
        user.setName(name);
        user.setImage(image);
        user.setRoles(userRoles);
        userRepository.save(user);

        // Generate JWT
        String jwtToken = jwtService.generateToken(user);

        response.sendRedirect("http://localhost:8080/auth-redirect.html?token=" + jwtToken);
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write("{\"token\": \"" + jwtToken + "\"}");

        // Redirect to Flutter app via deep link with token
        //response.sendRedirect("http://localhost:8080/user?token=" + jwtToken);
    }
}
