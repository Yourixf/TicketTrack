package nl.yourivb.TicketTrack.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import nl.yourivb.TicketTrack.dtos.auth.AuthResponseDto;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private String message;
    private int status;
    private String error;
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Map<String, String>> errors;

    public ApiResponse(String message, HttpStatus status, T data) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.status = status.value();
        this.error = status.isError() ? status.getReasonPhrase() : null;
        this.data = data;
    }

    public ApiResponse(String message, HttpStatus status, List<Map<String, String>> errors) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.data = null;
        this.errors = errors;
    }

    public ApiResponse(int value, String authenticationSuccessful, AuthResponseDto response) {
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public T getData() {
        return data;
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }
}
