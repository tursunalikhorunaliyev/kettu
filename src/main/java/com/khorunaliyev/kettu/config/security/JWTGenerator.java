package com.khorunaliyev.kettu.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JWTGenerator {

    /**
     * Signing secret. Externalised so it can be overridden per environment via the
     * {@code JWT_SECRET} environment variable (see application.yml -> {@code jwt.secret}).
     * The default below preserves the historically-used development key so existing
     * dev tokens stay valid; production MUST override it with a strong, private value.
     */
    private final String secretKey;

    /** Token lifetime in milliseconds (default ~40h, preserving prior behaviour). */
    private final long expirationMillis;

    public JWTGenerator(
            @Value("${jwt.secret:M1grationT0JWTwithGoogleOAuth2isSecure2025lllllll}") String secretKey,
            @Value("${jwt.expiration-ms:144000000}") long expirationMillis) {
        this.secretKey = secretKey;
        this.expirationMillis = expirationMillis;
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getSigningKey()).compact();
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("email", customUserDetails.getUsername());
            claims.put("roles", customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(auth -> auth.replace("ROLE_", "")) // remove prefix for token clarity
                    .collect(Collectors.toList()));
        }
        return generateToken(claims, userDetails);
    }




}
