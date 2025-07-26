package com.mwaf.productservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    @Autowired
//    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

            .csrf(csrf -> csrf.disable());
//            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
//                .requestMatchers(HttpMethod.PUT, "/api/products/*/reduceStock").authenticated()
//                .requestMatchers(HttpMethod.POST, "/api/products/**").permitAll()
//                .requestMatchers(HttpMethod.PUT, "/api/products/**").permitAll()
//                .requestMatchers(HttpMethod.DELETE, "/api/products/**").permitAll()
//                .anyRequest().authenticated()
////            )
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
