package com.mwaf.cartservice.controller;

import com.mwaf.cartservice.dto.CartItemRequest;
import com.mwaf.cartservice.dto.CartResponse;
import com.mwaf.cartservice.service.CartService;
import com.mwaf.cartservice.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItemQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody CartItemRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        CartResponse cart = cartService.updateItemQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @PathVariable Long productId,
            HttpServletRequest httpRequest) {
        
        Long userId = getUserIdFromRequest(httpRequest);
        CartResponse cart = cartService.removeItemFromCart(userId, productId);
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
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return Long.parseLong(jwtUtil.getUserId(token));
        }
        throw new RuntimeException("No valid JWT token found");
    }
} 