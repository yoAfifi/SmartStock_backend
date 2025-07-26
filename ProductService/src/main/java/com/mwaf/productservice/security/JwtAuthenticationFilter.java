//package com.mwaf.productservice.security;
//
//import com.mwaf.productservice.util.JwtUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        // Check if it's a GET request to /api/products - these are public
//        if (request.getMethod().equals("GET") && request.getRequestURI().startsWith("/api/products")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Get the Authorization header
//        String authHeader = request.getHeader("Authorization");
//
//        // If no Authorization header or not a Bearer token, continue to the next filter
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // Extract the token
//        String token = authHeader.substring(7);
//
//        try {
//            // Extract user information from token
//            String userId = jwtUtil.getUserId(token);
//            List<String> roles = jwtUtil.getRoles(token);
//
//            // Convert roles to Spring Security authorities
//            List<SimpleGrantedAuthority> authorities = roles.stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList());
//
//            // Create authentication token
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
//
//            // Set the authentication in the SecurityContext
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        } catch (Exception e) {
//            // If token validation fails, clear the security context
//            SecurityContextHolder.clearContext();
//        }
//
//        // Continue with the filter chain
//        filterChain.doFilter(request, response);
//    }
//}
