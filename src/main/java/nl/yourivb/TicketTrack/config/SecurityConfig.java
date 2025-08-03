package nl.yourivb.TicketTrack.config;

import javax.sql.DataSource;
import nl.yourivb.TicketTrack.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



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
 
        http 
                .csrf().disable() 
                .httpBasic().disable() 
                .cors().and() 
                .authorizeHttpRequests() 
                .requestMatchers(HttpMethod.GET, "/info").authenticated()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers("/users/**").hasAnyRole("ADMIN", "IT") 
                .requestMatchers("/interactions").hasAnyRole("ADMIN", "IT") 
                .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN") 
                .requestMatchers("/authenticate").permitAll() 
 
                .anyRequest().denyAll() 
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); 
        return http.build(); 
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
} 