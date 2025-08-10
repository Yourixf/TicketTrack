package nl.yourivb.TicketTrack.config;

import nl.yourivb.TicketTrack.payload.ApiResponse;
import nl.yourivb.TicketTrack.security.AppUserDetailsService;
import nl.yourivb.TicketTrack.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.sql.DataSource;
import java.util.List;


@Configuration
public class SecurityConfig {
  private final DataSource dataSource;
  private final JwtRequestFilter jwtRequestFilter;
  private final ObjectMapper objectMapper;


    public SecurityConfig(DataSource dataSource, JwtRequestFilter jwtRequestFilter, ObjectMapper objectMapper) {
        this.dataSource = dataSource;
        this.jwtRequestFilter = jwtRequestFilter;
        this.objectMapper = objectMapper;
    }

    @Bean 
    PasswordEncoder passwordEncoder(){ 
        return new BCryptPasswordEncoder(); 
    }


    @Bean
    protected SecurityFilterChain filter(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/**")
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, ex) -> {
                            var body = new ApiResponse<>("Unauthorized", HttpStatus.UNAUTHORIZED, null);
                            response.setStatus(401);
                            response.setContentType("application/json");
                            objectMapper.writeValue(response.getWriter(), body);
                        })
                        .accessDeniedHandler((request, response, ex) -> {
                            var body = new ApiResponse<>("Forbidden", HttpStatus.FORBIDDEN, null);
                            response.setStatus(403);
                            response.setContentType("application/json");
                            objectMapper.writeValue(response.getWriter(), body);
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/users/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/assignment-groups/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.POST, "/assignment-groups").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/assignment-groups/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/assignment-groups/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/assignment-groups/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/attachments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/attachments/**").hasAnyRole("ADMIN", "IT")

                        .requestMatchers(HttpMethod.GET, "/incidents/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/incidents/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.PUT, "/incidents/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.PATCH, "/incidents/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.DELETE, "/incidents/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/interactions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/interactions").authenticated()
                        .requestMatchers(HttpMethod.POST, "/interactions/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.PUT, "/interactions/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.PATCH, "/interactions/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.DELETE, "/interactions/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/notes/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/notes/**").hasRole("ADMIN")

                        .requestMatchers("/roles/**").denyAll()

                        .requestMatchers(HttpMethod.GET, "/service-offerings/**").hasAnyRole("ADMIN", "IT")
                        .requestMatchers(HttpMethod.POST, "/service-offerings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/service-offerings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/service-offerings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/service-offerings/**").hasRole("ADMIN")

                        .requestMatchers("/authenticate").anonymous()
                        .anyRequest().authenticated() // ✅ Fix: Allow authenticated requests
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }



    @Bean
    public AuthenticationManager authManager(HttpSecurity http,
                                             AppUserDetailsService uds) throws Exception {
        var auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(uds).passwordEncoder(passwordEncoder());
        return auth.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
} 