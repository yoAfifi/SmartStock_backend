package com.mwaf.cartservice.service;

import com.mwaf.cartservice.client.ProductServiceClient;
import com.mwaf.cartservice.dto.CartResponse;
import com.mwaf.cartservice.dto.CartItemResponse;
import com.mwaf.cartservice.dto.ProductInfo;
import com.mwaf.cartservice.model.Cart;
import com.mwaf.cartservice.model.CartItem;
import com.mwaf.cartservice.repository.CartRepository;
import com.mwaf.cartservice.repository.CartItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    
    @PersistenceContext
    private EntityManager entityManager;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductServiceClient productServiceClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productServiceClient = productServiceClient;
    }

    @Transactional
    public CartResponse getOrCreateCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItemToCart(Long userId, Long productId, Integer quantity) {
        // Validate product and stock
        ProductInfo productInfo = productServiceClient.getProductById(productId);
        if (productInfo.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + productInfo.getStockQuantity() + ", Requested: " + quantity);
        }

        Cart cart = getOrCreateCartEntity(userId);
        CartItem existingItem = cartItemRepository.findByCartAndProductId(cart, productId).orElse(null);

        if (existingItem != null) {
            // Product already exists in cart - throw specific exception
            throw new RuntimeException("Product is already in your cart. You can update the quantity using the + and - buttons.");
        } else {
            // Create new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(productInfo.getPrice());
            cartItemRepository.save(newItem);
        }

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItemQuantity(Long userId, Long itemId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Cart cart = getOrCreateCartEntity(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item with ID " + itemId + " not found"));

        // Verify the item belongs to this user's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item with ID " + itemId + " does not belong to user " + userId);
        }

        // Validate product and stock
        ProductInfo productInfo = productServiceClient.getProductById(item.getProductId());
        if (productInfo.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + productInfo.getStockQuantity() + ", Requested: " + quantity);
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long itemId) {
        try {
            Cart cart = getOrCreateCartEntity(userId);
            CartItem item = cartItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Cart item with ID " + itemId + " not found"));

            // Verify the item belongs to this user's cart
            if (!item.getCart().getId().equals(cart.getId())) {
                throw new RuntimeException("Cart item with ID " + itemId + " does not belong to user " + userId);
            }

            // Log the deletion for debugging
            System.out.println("Deleting cart item: " + itemId + " for user: " + userId);
            
            cartItemRepository.delete(item);
            
            // Force flush to ensure deletion is persisted
            cartItemRepository.flush();
            
            System.out.println("Successfully deleted cart item: " + itemId);
            
            return buildCartResponse(cart);
        } catch (Exception e) {
            System.err.println("Error removing cart item: " + itemId + " for user: " + userId + " - " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        try {
            System.out.println("=== CLEARING CART FOR USER: " + userId + " ===");
            
            // Method 1: Direct SQL query to delete all cart items by user ID
            System.out.println("Deleting cart items using direct SQL query...");
            cartItemRepository.deleteAllByUserId(userId);
            
            // Method 2: Also clear the cart entity as backup
            Cart cart = getOrCreateCartEntity(userId);
            System.out.println("Cart ID: " + cart.getId() + ", Items before clearing: " + cart.getItems().size());
            
            cart.getItems().clear();
            cartRepository.save(cart);
            
            // Force flush to ensure deletion is persisted
            cartItemRepository.flush();
            cartRepository.flush();
            
            // Force refresh the entity to get fresh data from database
            entityManager.refresh(cart);
            
            // Verify deletion by checking the cart again
            Cart updatedCart = getOrCreateCartEntity(userId);
            System.out.println("Items after clearing: " + updatedCart.getItems().size());
            
            System.out.println("=== CART SUCCESSFULLY CLEARED ===");
            
            return buildCartResponse(updatedCart);
        } catch (Exception e) {
            System.err.println("Error clearing cart for user: " + userId + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public CartResponse refreshPrices(Long userId) {
        Cart cart = getOrCreateCartEntity(userId);
        
        for (CartItem item : cart.getItems()) {
            ProductInfo productInfo = productServiceClient.getProductById(item.getProductId());
            item.setUnitPrice(productInfo.getPrice());
            cartItemRepository.save(item);
        }

        return buildCartResponse(cart);
    }

    private Cart getOrCreateCartEntity(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse buildCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::buildCartItemResponse)
                .collect(Collectors.toList());

        response.setItems(itemResponses);

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer totalItems = itemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        response.setTotalAmount(totalAmount);
        response.setTotalItems(totalItems);

        return response;
    }

    private CartItemResponse buildCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setQuantity(item.getQuantity());
        
        // Get product information from ProductService
        try {
            ProductInfo productInfo = productServiceClient.getProductById(item.getProductId());
            response.setProductName(productInfo.getName());
            response.setImageUrl(productInfo.getImageUrl());
            response.setStockQuantity(productInfo.getStockQuantity());
            
            // Use product price if cart item price is null or zero
            if (item.getUnitPrice() == null || item.getUnitPrice().compareTo(BigDecimal.ZERO) == 0) {
                response.setUnitPrice(productInfo.getPrice());
            } else {
                response.setUnitPrice(item.getUnitPrice());
            }
        } catch (Exception e) {
            // Fallback values if ProductService is unavailable
            response.setProductName("Product not available");
            response.setImageUrl("/placeholder-product.jpg");
            response.setStockQuantity(0);
            response.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO);
        }
        
        // Calculate total price
        BigDecimal unitPrice = response.getUnitPrice() != null ? response.getUnitPrice() : BigDecimal.ZERO;
        response.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));

        return response;
    }
} 