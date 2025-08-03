package nl.yourivb.TicketTrack.controllers;

import nl.yourivb.TicketTrack.security.JwtUtil;
import nl.yourivb.TicketTrack.security.CustomUserDetailsService;
import nl.yourivb.TicketTrack.dtos.Auth.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public AuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // @PostMapping("/authenticate")
    // public ResponseEntity<?> createAuthenticationToken(@RequestBody nl.yourivb.TicketTrack.dtos.Auth.AuthRequest authRequest) {
    //     System.out.println("Authenticating user: " + authRequest.getEmail() + " " + authRequest.getPassword());
    //     Authentication authentication = authenticationManager.authenticate(
    //             new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
    //     );

    //     UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
    //     String jwt = jwtUtil.generateToken(userDetails);

    //     return ResponseEntity.ok(new AuthResponse(jwt));
    // }

    @PostMapping("/authenticate")
public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
    try {
        System.out.println("Authenticating user: " + authRequest.getEmail() + " / " + authRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(),
                authRequest.getPassword()
            )
        );

        System.out.println("Authentication successful for: " + authRequest.getEmail());

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(403).body("Authentication failed: " + e.getMessage());
    }
}
}
