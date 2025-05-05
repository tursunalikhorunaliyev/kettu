package com.khorunaliyev.kettu.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        System.out.println("SALOM");
        System.out.println(authException.getClass());

        String message;

        if (authException instanceof UsernameNotFoundException) {
            message = "User not found";
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        }
        else{
            message = "Missing or invalid token";
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        response.getWriter().write(new ObjectMapper().writeValueAsString(
                Map.of(
                        "error", "UNAUTHORIZED",
                        "message", message
                )
        ));
    }
}
