package nl.yourivb.TicketTrack.config;

import javax.sql.DataSource;
import nl.yourivb.TicketTrack.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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


@Configuration
public class SecurityConfig { 
  private final DataSource dataSource; 
  
 private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(DataSource dataSource, JwtRequestFilter jwtRequestFilter) {
        this.dataSource = dataSource;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean 
    PasswordEncoder passwordEncoder(){ 
        return new BCryptPasswordEncoder(); 
    } 
 
 
    @Bean 
    protected SecurityFilterChain filter (HttpSecurity http) throws Exception {

        return http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.GET, "/users/**").authenticated() // users can make interactions for other employees (openedFor)
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
                        .requestMatchers(HttpMethod.DELETE, "/attachments/**").hasRole("ADMIN")

                        .requestMatchers("/interactions").hasAnyRole("ADMIN", "IT")
                        .requestMatchers("/authenticate").anonymous()
                        .anyRequest().denyAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    } 
        
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.jdbcAuthentication().dataSource(dataSource)
            .usersByUsernameQuery("SELECT email, password, true FROM app_user WHERE email=?")
            .authoritiesByUsernameQuery("SELECT u.email, r.name " +
                "FROM app_user u JOIN role r ON u.role_id = r.id WHERE u.email=?");
        return authenticationManagerBuilder.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
} 