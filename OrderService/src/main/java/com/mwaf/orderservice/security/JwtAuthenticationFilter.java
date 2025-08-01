package com.mwaf.orderservice.security;

import com.mwaf.orderservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Get the Authorization header
        String authHeader = request.getHeader("Authorization");
        
        // If no Authorization header or not a Bearer token, continue to the next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract the token
        String token = authHeader.substring(7);
        
        try {
            // Extract user information from token
            String userId = jwtUtil.getUserIdFromToken(token);
            Long customerId = jwtUtil.getCustomerIdFromToken(token);
            List<String> roles = jwtUtil.getRolesFromToken(token);
            
            // Convert roles to Spring Security authorities
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            
            // Create authentication token with userId as principal and token as credentials
            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, token, authorities);
            
            // Add customerId as a detail in the authentication
            Map<String, Object> details = new HashMap<>();
            details.put("customerId", customerId);
            authentication.setDetails(details);
            
            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (Exception e) {
            // If token validation fails, clear the security context
            SecurityContextHolder.clearContext();
        }
        
        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
