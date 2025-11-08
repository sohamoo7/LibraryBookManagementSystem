package org.example.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application that centralizes exception handling
 * across all @RequestMapping methods through @ExceptionHandler methods.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Creates a standardized error response map.
     *
     * @param status  HTTP status code
     * @param error   Error type
     * @param message Error message
     * @param path    Request path
     * @return Map containing error details
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String error, String message, String path) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        if (path != null) {
            errorResponse.put("path", path);
        }
        return errorResponse;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        Map<String, Object> body = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                errors,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        
        String error = "Malformed JSON request";
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            error = String.format("Invalid value '%s' for field '%s'. %s",
                    ife.getValue(),
                    ife.getPath().get(ife.getPath().size() - 1).getFieldName(),
                    ife.getOriginalMessage());
        }

        Map<String, Object> body = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid JSON",
                error,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        
        String error = ex.getParameterName() + " parameter is missing";
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Missing Parameter",
                error,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<Object> handleBookNotAvailableException(BookNotAvailableException ex, WebRequest request) {
        logger.warn("Book not available: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.CONFLICT,
                "Book Not Available",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
        logger.warn("Resource already exists: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.CONFLICT,
                "Resource Already Exists",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BookAlreadyBorrowedException.class)
    public ResponseEntity<Object> handleBookAlreadyBorrowedException(BookAlreadyBorrowedException ex, WebRequest request) {
        logger.warn("Book already borrowed: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.CONFLICT,
                "Book Already Borrowed",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BorrowLimitExceededException.class)
    public ResponseEntity<Object> handleBorrowLimitExceededException(BorrowLimitExceededException ex, WebRequest request) {
        logger.warn("Borrow limit exceeded: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.FORBIDDEN,
                "Borrow Limit Exceeded",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        logger.error("Entity not found: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.NOT_FOUND,
                "Entity Not Found",
                "The requested resource was not found",
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        logger.error("Data integrity violation: {}", ex.getMessage());
        
        String message = "A data integrity violation occurred";
        if (ex.getCause() instanceof ConstraintViolationException) {
            message = "A database constraint was violated: " + ex.getCause().getMessage();
        }
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.CONFLICT,
                "Data Integrity Error",
                message,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("Illegal argument: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        logger.error("Validation error: {}", ex.getMessage());
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                ex.getMessage(),
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        String error = ex.getName() + " should be of type " + 
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName();
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Type Mismatch",
                error,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex, WebRequest request) {
        logger.error("Transaction error: {}", ex.getMessage());
        
        // Get the root cause
        Throwable rootCause = ex.getRootCause();
        String message = "A transaction error occurred";
        
        if (rootCause != null) {
            message = rootCause.getMessage();
            logger.error("Root cause: {}", rootCause.getMessage());
        }
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Transaction Error",
                message,
                ((ServletWebRequest) request).getRequest().getRequestURI()
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex, WebRequest request) {
        String errorPath = request instanceof ServletWebRequest 
                ? ((ServletWebRequest) request).getRequest().getRequestURI() 
                : "";
                
        logger.error("Unhandled error occurred: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                errorPath
        );
        
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
