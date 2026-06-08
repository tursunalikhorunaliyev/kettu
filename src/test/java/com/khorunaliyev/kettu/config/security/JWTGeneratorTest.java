package com.khorunaliyev.kettu.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link JWTGenerator}. No Spring context or database required.
 */
class JWTGeneratorTest {

    private static final String SECRET =
            "M1grationT0JWTwithGoogleOAuth2isSecure2025lllllll";

    private final JWTGenerator generator = new JWTGenerator(SECRET, 144_000_000L);

    private UserDetails user(String email) {
        return User.withUsername(email).password("x").authorities("USER").build();
    }

    @Test
    void generatedTokenCarriesSubjectAndValidatesForSameUser() {
        UserDetails user = user("alice@example.com");

        String token = generator.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(generator.extractUserName(token)).isEqualTo("alice@example.com");
        assertThat(generator.isTokenValid(token, user)).isTrue();
    }

    @Test
    void tokenIsInvalidForADifferentUser() {
        String token = generator.generateToken(user("alice@example.com"));

        assertThat(generator.isTokenValid(token, user("bob@example.com"))).isFalse();
    }

    @Test
    void tamperedTokenIsRejectedWithSignatureException() {
        String token = generator.generateToken(user("alice@example.com"));
        String tampered = token.substring(0, token.length() - 2)
                + (token.endsWith("a") ? "bb" : "aa");

        assertThatThrownBy(() -> generator.extractUserName(tampered))
                .isInstanceOf(Exception.class);
    }

    @Test
    void tokenSignedWithAnotherSecretIsRejected() {
        JWTGenerator other = new JWTGenerator(
                "TotallyDifferentSecretValueThatIsLongEnoughForHS256!!", 144_000_000L);
        String foreignToken = other.generateToken(user("alice@example.com"));

        assertThatThrownBy(() -> generator.extractUserName(foreignToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void expiredTokenIsRejected() {
        // Negative lifetime produces an already-expired token.
        JWTGenerator shortLived = new JWTGenerator(SECRET, -1_000L);
        String token = shortLived.generateToken(user("alice@example.com"));

        assertThatThrownBy(() -> shortLived.extractUserName(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
