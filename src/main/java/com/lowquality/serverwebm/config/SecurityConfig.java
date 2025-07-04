package com.lowquality.serverwebm.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.lowquality.serverwebm.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] restrictedApis = {
                "/api/manga/**", "/api/categories/**", "/api/authors/**"
        };
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth ->

                                auth

                        // Các endpoint auth cho phép tất cả
                        .requestMatchers("/api/auth/**").permitAll()


                        .requestMatchers("/api/users/**","/api/favorites/**").authenticated()

                        // Các API public cho phép GET
                        .requestMatchers(HttpMethod.GET, "/api/manga/**", "/api/categories/**", "/api/authors/**","/api/comments/**").permitAll()

                        // Các API này: POST, PUT, DELETE chỉ ADMIN và MOD, UPLOADER
//                        các api chapter và page nằm trong manga
                        .requestMatchers(HttpMethod.POST, restrictedApis).hasAnyRole("ADMIN", "MOD", "UPLOADER")
                        .requestMatchers(HttpMethod.PUT,  restrictedApis).hasAnyRole("ADMIN", "MOD", "UPLOADER")
                        .requestMatchers(HttpMethod.DELETE, restrictedApis).hasAnyRole("ADMIN", "MOD", "UPLOADER")
                        // Mọi request khác ko cần xác thực
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}