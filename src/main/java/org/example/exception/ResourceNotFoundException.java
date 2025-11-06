// src/main/java/org/example/exception/ResourceNotFoundException.java
package org.example.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}