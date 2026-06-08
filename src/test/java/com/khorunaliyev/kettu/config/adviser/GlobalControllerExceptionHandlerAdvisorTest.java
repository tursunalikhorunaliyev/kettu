package com.khorunaliyev.kettu.config.adviser;

import com.khorunaliyev.kettu.dto.reponse.Response;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalControllerExceptionHandlerAdvisorTest {

    private final GlobalControllerExceptionHandlerAdvisor advisor =
            new GlobalControllerExceptionHandlerAdvisor();

    @Test
    void genericHandlerReturns500AndDoesNotLeakInternalDetails() {
        Exception internal = new IllegalStateException(
                "jdbc:postgresql://db:5432 password=supersecret connection failed");

        ResponseEntity<Response> result = advisor.handleGeneric(internal);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        String message = result.getBody().data().toString();
        assertThat(message)
                .doesNotContain("supersecret")
                .doesNotContain("jdbc:postgresql")
                .doesNotContain("password");
    }

    @Test
    void resourceNotFoundMapsTo404() {
        ResponseEntity<Response> result =
                advisor.handleNotFound(new ResourceNotFoundException("Place not found"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody().data()).isEqualTo("Place not found");
    }

    @Test
    void accessDeniedMapsTo403() {
        ResponseEntity<Response> result =
                advisor.handleAccessDeniedException(new AccessDeniedException("nope"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
