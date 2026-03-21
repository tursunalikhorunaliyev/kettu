package com.khorunaliyev.kettu.config.adviser;

import com.khorunaliyev.kettu.dto.reponse.Response;
import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import org.springframework.security.access.AccessDeniedException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalControllerExceptionHandlerAdvisor {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGeneric(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response("error", "Server error: " + ex.getMessage()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Response> handleMissingHeader(MissingRequestHeaderException ex) {
        return ResponseEntity.badRequest().body(new Response("Required header param", false));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .badRequest()
                .body(new Response("Failed", "Missing required parameter " + ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .badRequest()
                .body(new Response("Failed", "Invalid parameter type " + ex.getName()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new Response("Failed", "Ma'lumot topilmadi"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Response("Invalid request body. Expected application/json format.", null));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Response> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(new Response("Unsupported content type. Please use application/json.", null));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response("Failed", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("You do not have permission to access this resource", "Forbidden"));
    }


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ex.getMessage(), "User Not Found"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("Invalid username or password", "Unauthorized"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new Response("Request method '" + ex.getMethod() + "' is not supported for this endpoint", "Method Not Allowed"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Response> handleNoHandlerFound(NoHandlerFoundException ex) {
        String message = "Endpoint topilmadi: " + ex.getHttpMethod() + " " + ex.getRequestURL();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new Response("Not Found", message));
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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response> handleConflict(DataIntegrityViolationException ex) {
        String message = "Berilgan ma'lumot noto'g'ri";

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
