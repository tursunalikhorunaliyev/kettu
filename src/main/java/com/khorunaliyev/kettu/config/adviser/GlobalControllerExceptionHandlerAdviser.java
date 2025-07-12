package com.khorunaliyev.kettu.config.adviser;

import com.khorunaliyev.kettu.dto.reponse.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalControllerExceptionHandlerAdviser {

    // 400 Bad Request: JSON parse error or body missing
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Response("Invalid request body. Expected application/json format.", null));
    }

    // 415 Unsupported Media Type: wrong Content-Type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Response> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new Response("Unsupported content type. Please use application/json.", null));
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response(ex.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Forbidden");
        response.put("message", "You do not have permission to access this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(UsernameNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "User Not Found");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Unauthorized");
        response.put("message", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
//        Map<String, String> response = new HashMap<>();
//        response.put("error", "Internal Server Error");
//        response.put("message", ex.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Method Not Allowed");
        response.put("message", "Request method '" + ex.getMethod() + "' is not supported for this endpoint");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Not Found");
        errorDetails.put("message", "The requested resource was not found");
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleConflict(DataIntegrityViolationException ex) {
        String message = "Data integrity violation";

        Throwable rootCause = getRootCause(ex);
        if (rootCause instanceof SQLIntegrityConstraintViolationException) {
            String sqlMessage = rootCause.getMessage(); // this contains the DB error text

            if (sqlMessage != null) {
                // Example: Duplicate entry 'USA' for key 'country.code_UNIQUE'
                Pattern pattern = Pattern.compile("key '(.+?)'");
                Matcher matcher = pattern.matcher(sqlMessage);

                if (matcher.find()) {
                    String constraint = matcher.group(1); // e.g., country.code_UNIQUE
                    String table = extractTableNameFromConstraint(constraint);
                    message = "Duplicate value violates unique constraint on table: " + table;
                } else {
                    message = "Unique constraint violation: " + sqlMessage;
                }
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Response("Error", message));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(new Response("Validation error", errors));
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        return (cause == null || cause == throwable) ? throwable : getRootCause(cause);
    }

    private String extractTableNameFromConstraint(String constraint) {
        // This is optional, based on naming convention like table_column_UNIQUE
        if (constraint.contains(".")) {
            return constraint.split("\\.")[0]; // Extract 'country' from 'country.code_UNIQUE'
        }
        return "unknown";
    }
}
