package nl.yourivb.TicketTrack.exceptions;

import nl.yourivb.TicketTrack.payload.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import java.lang.IllegalStateException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleRecordNotFound(RecordNotFoundException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), HttpStatus.NOT_FOUND, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()
                ))
                .toList();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        ApiResponse<Object> response = new ApiResponse<>("Validation failed", HttpStatus.BAD_REQUEST, errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadException(BadRequestException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>("Invalid or missing request body", HttpStatus.BAD_REQUEST, null),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiResponse<Object>> handleFileStorageException(FileStorageException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), HttpStatus.UNAUTHORIZED, null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), ex.getStatusCode(), null);
        return new ResponseEntity<>(response, ex.getStatusCode());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>("Invalid email or password", HttpStatus.UNAUTHORIZED, null),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuth(AuthenticationException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>("Authentication failed", HttpStatus.UNAUTHORIZED, null),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiResponse<Object>> handleInternalAuthException(InternalAuthenticationServiceException ex) {
        return new ResponseEntity<>(
                new ApiResponse<>(ex.getMessage(), HttpStatus.UNAUTHORIZED, null),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtAuthenticationException(JwtAuthenticationException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), HttpStatus.UNAUTHORIZED, null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<Object> response = new ApiResponse<>("Access denied: " +
                ex.getMessage(), HttpStatus.FORBIDDEN, null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>("Illegal argument: " +
                ex.getMessage(), HttpStatus.BAD_REQUEST, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ApiResponse<Object> response = new ApiResponse<>("Database constraint violated: " +
                ex.getMostSpecificCause().getMessage(), HttpStatus.CONFLICT, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
    public ResponseEntity<ApiResponse<Object>> handleMultipart(MultipartException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
