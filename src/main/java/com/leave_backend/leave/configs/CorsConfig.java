package com.leave_backend.leave.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
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
        return new CorsFilter(source);
    }
}
