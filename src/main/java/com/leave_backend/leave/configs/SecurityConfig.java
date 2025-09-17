package com.leave_backend.leave.configs;

import com.leave_backend.leave.filters.JwtAuthFilter;
import com.leave_backend.leave.services.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // Allow specific origins
        // config.addAllowedOrigin("*");
//        config.addAllowedOriginPattern("https://*.azurewebsites.net");
//        config.addAllowedOriginPattern("https://*.netlify.app");
//        config.addAllowedOriginPattern("https://*.vercel.app");
//        config.addAllowedOriginPattern("https://*.herokuapp.com");
//        config.addAllowedOriginPattern("https://*.firebaseapp.com");
//        config.addAllowedOriginPattern("https://*.github.io");
//        config.addAllowedOriginPattern("https://*.gitlab.io");
//        config.addAllowedOriginPattern("https://*.onrender.com");
//        config.addAllowedOriginPattern("https://*.surge.sh");
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:5000");
        config.addAllowedOrigin("http://localhost:5001");

        // Allow all headers and methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // If you want specific headers or methods, uncomment below
        // config.addAllowedHeader("Content-Type");
        // config.addAllowedHeader("Authorization");
        // config.addAllowedMethod("GET");
        // config.addAllowedMethod("POST");
        // config.addAllowedMethod("PUT");
        // config.addAllowedMethod("DELETE");
        // config.addAllowedMethod("OPTIONS");

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
