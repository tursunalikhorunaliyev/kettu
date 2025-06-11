package com.khorunaliyev.kettu.config.adviser;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Optional: if you want to include a cause (another exception)
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
