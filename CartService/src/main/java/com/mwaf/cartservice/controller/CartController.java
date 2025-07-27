package com.mwaf.cartservice.controller;

import com.mwaf.cartservice.dto.CartItemRequest;
import com.mwaf.cartservice.dto.CartResponse;
import com.mwaf.cartservice.service.CartService;
import com.mwaf.cartservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;

    public CartController(CartService cartService, JwtUtil jwtUtil) {
        this.cartService = cartService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        CartResponse cart = cartService.getOrCreateCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItemToCart(
            @Valid @RequestBody CartItemRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        CartResponse cart = cartService.addItemToCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long itemId,
            @RequestParam Integer quantity,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        CartResponse cart = cartService.updateItemQuantity(userId, itemId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @PathVariable Long itemId,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        CartResponse cart = cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearCart(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        CartResponse cart = cartService.clearCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/refresh-prices")
    public ResponseEntity<CartResponse> refreshPrices(HttpServletRequest httpRequest) {
        Long userId = getUserIdFromRequest(httpRequest);
        CartResponse cart = cartService.refreshPrices(userId);
        return ResponseEntity.ok(cart);
    }

    private Long getUserIdFromRequest(HttpServletRequest request) {
        // First try to get user ID from Gateway Service headers
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            return Long.parseLong(userIdHeader);
        }
        
        // Fallback to JWT token extraction (for direct service calls)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return Long.parseLong(jwtUtil.getUserId(token));
        }
        
        throw new RuntimeException("No valid user ID found in request");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
} 