package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.security.JwtUtil;
import nl.yourivb.TicketTrack.security.AppUserDetailsService;
import nl.yourivb.TicketTrack.dtos.Auth.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AppUserDetailsService userDetailsService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, AppUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthResponseDto>> createAuthenticationToken(@RequestBody AuthRequestDto authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        Date jwtExpiresAt = jwtUtil.extractExpiration(jwt);

        AuthResponseDto authResponse = new AuthResponseDto(jwt, jwtExpiresAt);

        return new ResponseEntity<>(
                new ApiResponse<>("Authentication successful", HttpStatus.OK, authResponse), HttpStatus.OK
        );
    }
}
