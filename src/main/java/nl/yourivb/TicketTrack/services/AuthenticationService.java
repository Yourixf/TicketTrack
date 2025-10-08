package nl.yourivb.TicketTrack.services;

import nl.yourivb.TicketTrack.dtos.auth.AuthRequestDto;
import nl.yourivb.TicketTrack.dtos.auth.AuthResponseDto;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import nl.yourivb.TicketTrack.repositories.AppUserRepository;
import nl.yourivb.TicketTrack.security.AppUserDetails;
import nl.yourivb.TicketTrack.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthenticationService(AppUserRepository appUserRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.appUserRepository = appUserRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDto authenticate(AuthRequestDto authRequest) {
        appUserRepository.findByEmail(authRequest.getEmail()).orElseThrow(() -> new RecordNotFoundException(authRequest.getEmail() + " not found in database"));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),
                        authRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Long userId = ((AppUserDetails) authentication.getPrincipal()).getAppUser().getId();
        String jwt = jwtUtil.generateToken(userDetails, userId);
        Date jwtExpiresAt = jwtUtil.extractExpiration(jwt);

        return new AuthResponseDto(jwt, jwtExpiresAt);
    }

}
