package com.khorunaliyev.kettu.config.security;

import com.khorunaliyev.kettu.entity.auth.AppUser;
import com.khorunaliyev.kettu.entity.auth.Role;
import com.khorunaliyev.kettu.repository.auth.RoleRepository;
import com.khorunaliyev.kettu.repository.auth.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTGenerator jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String platform = "mobile";

        if(request.getCookies()!=null){
            for (Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("oauth_platform")){
                    platform = cookie.getValue();
                    break;
                }
            }
        }

        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String image = oauthUser.getAttribute("picture");


        // Save or update user
        Optional<AppUser> optionalAppUser = userRepository.findByEmail(email);
        AppUser appUser = optionalAppUser.orElseGet(AppUser::new);
        if (optionalAppUser.isEmpty()) {
            System.out.println("-----------------------------------------------------------");
            Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
                Role role = new Role();
                role.setName("USER");
                return roleRepository.save(role);
            });
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ADMIN");
                return roleRepository.save(role);
            });

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);

            assert email != null;
            if (email.equals("khorunaliyev@gmail.com")) {
                userRoles.add(adminRole);
            }

            appUser.setEmail(email);
            appUser.setName(name);
            if (image != null) {
                appUser.setImage(image.substring(0, image.lastIndexOf("=")) + "=s1024");
            }
            appUser.setRoles(userRoles);
            userRepository.save(appUser);
        }

        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + appUser.getEmail());

        // Generate JWT
        String jwtToken = jwtService.generateToken(appUser);

        Cookie clearCookie = new Cookie("oauth_platform", null);
        clearCookie.setPath("/");
        clearCookie.setHttpOnly(true);
        clearCookie.setMaxAge(0);
        response.addCookie(clearCookie);

        response.sendRedirect("http://localhost:8080/auth-redirect.html?token=" + jwtToken + "&platform=" + platform);
    }
}
