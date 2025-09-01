package nl.yourivb.TicketTrack.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.yourivb.TicketTrack.exceptions.JwtAuthenticationException;
import nl.yourivb.TicketTrack.exceptions.RecordNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AppUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(AppUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // if no bearer, go with default auth
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        Long userId;

        // token validation
        try {
            userId = jwtUtil.extractUserIdAsLong(jwt);

            if (!jwtUtil.validateTokenForUserId(jwt, userId)) {
                throw new JwtAuthenticationException("JWT is expired or invalid for user");
            }

        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", new JwtAuthenticationException("JWT token is expired"));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (JwtException e) {
            request.setAttribute("exception", new JwtAuthenticationException("Invalid or malformed JWT token"));
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // authentication
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserById(userId);

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (UsernameNotFoundException e) {
                throw new RecordNotFoundException("User with ID " + userId + " not found");
            }
        }

        filterChain.doFilter(request, response);
    }

}
