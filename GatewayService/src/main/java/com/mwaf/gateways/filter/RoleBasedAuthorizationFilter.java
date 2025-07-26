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
//import java.util.Map;
//
//@Component
//public class RoleBasedAuthorizationFilter implements GlobalFilter, Ordered {
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    // Define endpoints that require admin role
//    private final Map<String, List<String>> protectedEndpoints = Map.of(
//            "/api/products/POST", List.of("ROLE_ADMIN"),
//            "/api/products/PUT", List.of("ROLE_ADMIN"),
//            "/api/products/DELETE", List.of("ROLE_ADMIN"),
//            "/api/customers/GET", List.of("ROLE_ADMIN","ROLE_USER"),
//            "/api/customers/POST", List.of("ROLE_ADMIN"),
//            "/api/customers/PUT", List.of("ROLE_ADMIN"),
//            "/api/customers/DELETE", List.of("ROLE_ADMIN")
//    );
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String path = request.getPath().toString();
//        String method = request.getMethod().toString();
//
//        // Skip role check for public endpoints
//        if (!requiresRoleCheck(path, method)) {
//            return chain.filter(exchange);
//        }
//
//        // Get Authorization header
//        String authHeader = request.getHeaders().getFirst("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return chain.filter(exchange); // Let the JWT filter handle this error
//        }
//
//        String token = authHeader.substring(7);
//        List<String> userRoles = jwtUtil.getRoles(token);
//
//        // Check if user has required role for this endpoint
//        if (!hasRequiredRole(path, method, userRoles)) {
//            return onError(exchange, "Insufficient permissions", HttpStatus.FORBIDDEN);
//        }
//
//        return chain.filter(exchange);
//    }
//
//    private boolean requiresRoleCheck(String path, String method) {
//        String key = getEndpointKey(path, method);
//        return protectedEndpoints.containsKey(key);
//    }
//
//    private boolean hasRequiredRole(String path, String method, List<String> userRoles) {
//        String key = getEndpointKey(path, method);
//
//        List<String> requiredRoles = protectedEndpoints.get(key);
//        if (requiredRoles == null) {
//            return true; // No specific roles required
//        }
//
//        // Check if the user has any of the required roles
//        return userRoles.stream().anyMatch(requiredRoles::contains);
//    }
//
//    private String getEndpointKey(String path, String method) {
//        if (path.startsWith("/api/products")) {
//            return "/api/products/" + method;
//        } else if (path.startsWith("/api/customers")) {
//            return "/api/customers/" + method;
//        }
//        return path;
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
//        return 0; // Run after JWT validation
//    }
//}
