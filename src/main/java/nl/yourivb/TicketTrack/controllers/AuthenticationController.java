package nl.yourivb.TicketTrack.controllers;

import jakarta.validation.Valid;
import nl.yourivb.TicketTrack.dtos.auth.AuthRequestDto;
import nl.yourivb.TicketTrack.dtos.auth.AuthResponseDto;
import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticate")
public class AuthenticationController {


    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponseDto>> createAuthenticationToken(@Valid @RequestBody AuthRequestDto authRequest) {
        AuthResponseDto authResponse = authenticationService.authenticate(authRequest);

        return ResponseEntity.ok(
                new ApiResponse<>("Authentication successful", HttpStatus.OK, authResponse)
        );
    }

}
