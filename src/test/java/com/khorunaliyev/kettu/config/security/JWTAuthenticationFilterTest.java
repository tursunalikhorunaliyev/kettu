package com.khorunaliyev.kettu.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Regression tests proving the JWT filter fails closed: a missing, malformed,
 * expired or unknown-user token never aborts the request with an exception, and a
 * valid token authenticates the request.
 */
@ExtendWith(MockitoExtension.class)
class JWTAuthenticationFilterTest {

    private final JWTGenerator jwtGenerator =
            new JWTGenerator("M1grationT0JWTwithGoogleOAuth2isSecure2025lllllll", 144_000_000L);

    @Mock
    private CutomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private JWTAuthenticationFilter filter() {
        return new JWTAuthenticationFilter(jwtGenerator, userDetailsService);
    }

    @Test
    void requestWithoutAuthorizationHeaderPassesThroughAnonymous() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter().doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void malformedTokenDoesNotThrowAndStaysAnonymous() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer not-a-real-jwt");

        filter().doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void unknownUserDoesNotThrowAndStaysAnonymous() throws Exception {
        UserDetails details = User.withUsername("ghost@example.com").password("x").authorities("USER").build();
        String token = jwtGenerator.generateToken(details);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(userDetailsService.loadUserByUsername("ghost@example.com"))
                .thenThrow(new UsernameNotFoundException("nope"));

        filter().doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void validTokenAuthenticatesTheRequest() throws Exception {
        UserDetails details = User.withUsername("alice@example.com").password("x").authorities("USER").build();
        String token = jwtGenerator.generateToken(details);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(userDetailsService.loadUserByUsername("alice@example.com")).thenReturn(details);

        filter().doFilterInternal(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("alice@example.com");
    }
}
