//package com.mwaf.gateways.filter;
//
//import com.mwaf.gateways.utility.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    // Define public endpoints that don't require authentication
//    private final List<String> publicEndpoints = List.of(
//        "/api/auth/register",
//        "/api/auth/login",
//        "/api/auth/admin/register"
//    );
//
//    // Define endpoints that are publicly readable but require auth for modifications
//    private final List<String> publicReadEndpoints = List.of(
//        "/api/products"
//    );
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getPath().toString();
//        String method = request.getMethod().toString();
//
//        // Skip validation for public endpoints
//        if (isPublicEndpoint(path)) {
//            return chain.filter(exchange);
//        }
//
//        // Allow GET requests to public read endpoints
//        if (isPublicReadEndpoint(path) && "GET".equals(method)) {
//            return chain.filter(exchange);
//        }
//
//        // Check for Authorization header
//        if (!request.getHeaders().containsKey("Authorization")) {
//            return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
//        }
//
//        String authHeader = request.getHeaders().getFirst("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
//        }
//
//        // Extract and validate the token
//        String token = authHeader.substring(7);
//        if (!jwtUtil.validateToken(token)) {
//            return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
//        }
//
//        // Add user info to request headers for downstream services
//        ServerHttpRequest modifiedRequest = request.mutate()
//            .header("X-User-Id", jwtUtil.getUserId(token))
//            .header("X-Customer-Id", String.valueOf(jwtUtil.getCustomerId(token)))
//            .header("X-User-Roles", String.join(",", jwtUtil.getRoles(token)))
//            .build();
//
//        return chain.filter(exchange.mutate().request(modifiedRequest).build());
//    }
//
//    private boolean isPublicEndpoint(String path) {
//        return publicEndpoints.stream().anyMatch(path::startsWith);
//    }
//
//    private boolean isPublicReadEndpoint(String path) {
//        return publicReadEndpoints.stream().anyMatch(path::startsWith);
//    }
//
//    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(status);
//        return response.setComplete();
//    }
//
//    @Override
//    public int getOrder() {
//        return -1; // High priority
//    }
//}
