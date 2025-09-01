package nl.yourivb.TicketTrack.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.yourivb.TicketTrack.exceptions.JwtAuthenticationException;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException {
        // this takes the exception out of the request
        Exception ex = (Exception) request.getAttribute("exception");

        // defines default message
        String message = "Unauthorized";

        // if it's an JWT error, get the specific error
        if (ex instanceof JwtAuthenticationException) {
            message = ex.getMessage();
        }

        // wraps and sends the exception
        ApiResponse<Object> body = new ApiResponse<>(message, HttpStatus.UNAUTHORIZED, null);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), body);
    }
}
