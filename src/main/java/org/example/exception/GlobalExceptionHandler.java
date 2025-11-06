package org.example.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application that centralizes exception handling
 * across all @RequestMapping methods through @ExceptionHandler methods.
 * 
 * This class extends ResponseEntityExceptionHandler to provide centralized exception
 * handling across all @RequestMapping methods through @ExceptionHandler methods.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles validation exceptions thrown when @Validated fails.
     * 
     * @param ex      the exception to handle
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a ResponseEntity with the error details
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        // Create a response body with error details
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());  // When the error occurred
        body.put("status", status.value());          // HTTP status code
        body.put("error", "Validation Error");       // General error type

        // Extract and format all validation error messages
        String errors = ex.getBindingResult()
                .getFieldErrors()                      // Get all field errors
                .stream()
                .map(FieldError::getDefaultMessage)     // Extract error messages
                .collect(Collectors.joining(", "));    // Join multiple errors with comma

        body.put("message", errors);                  // Add formatted error messages

        // Return response with BAD_REQUEST status and error details
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ResourceNotFoundException when a requested resource is not found.
     * 
     * @param ex the exception containing the error message
     * @return ResponseEntity with NOT_FOUND status and error details
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles BookNotAvailableException when a book is not available for borrowing.
     * 
     * @param ex the exception containing the error message
     * @return ResponseEntity with CONFLICT status and error details
     */
    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<Object> handleBookNotAvailableException(
            BookNotAvailableException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    /**
     * Handles ResourceAlreadyExistsException when attempting to create a resource that already exists.
     * 
     * @param ex the exception containing the error message
     * @return ResponseEntity with CONFLICT status and error details
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex) {
        return buildErrorResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BorrowLimitExceededException.class)
    public ResponseEntity<Object> handleBorrowLimitExceededException(
            BorrowLimitExceededException ex) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN);
    }

    /**
     * Global exception handler for all unhandled exceptions.
     * 
     * @param ex the caught exception
     * @return ResponseEntity with INTERNAL_SERVER_ERROR status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(
            Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Builds a standardized error response for all exceptions.
     * 
     * @param exception  the exception to build response for
     * @param httpStatus the HTTP status code to return
     * @return ResponseEntity with the error details and specified status
     */
    private ResponseEntity<Object> buildErrorResponse(
            Exception exception, HttpStatus httpStatus) {
        // Create a consistent error response structure
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());  // When the error occurred
        body.put("status", httpStatus.value());      // HTTP status code
        body.put("error", httpStatus.getReasonPhrase());  // Status text
        body.put("message", exception.getMessage());      // Error message

        return new ResponseEntity<>(body, httpStatus);
    }
}
