package com.mwaf.cartservice.service;

import com.mwaf.cartservice.client.ProductServiceClient;
import com.mwaf.cartservice.dto.CartResponse;
import com.mwaf.cartservice.dto.CartItemResponse;
import com.mwaf.cartservice.dto.ProductInfo;
import com.mwaf.cartservice.model.Cart;
import com.mwaf.cartservice.model.CartItem;
import com.mwaf.cartservice.repository.CartRepository;
import com.mwaf.cartservice.repository.CartItemRepository;
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
            // Update existing item
            int newQuantity = existingItem.getQuantity() + quantity;
            if (productInfo.getStockQuantity() < newQuantity) {
                throw new RuntimeException("Insufficient stock for updated quantity. Available: " + productInfo.getStockQuantity() + ", Requested: " + newQuantity);
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
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
    public CartResponse updateItemQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // Validate product and stock
        ProductInfo productInfo = productServiceClient.getProductById(productId);
        if (productInfo.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + productInfo.getStockQuantity() + ", Requested: " + quantity);
        }

        Cart cart = getOrCreateCartEntity(userId);
        CartItem item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateCartEntity(userId);
        CartItem item = cartItemRepository.findByCartAndProductId(cart, productId)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cartItemRepository.delete(item);
        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCartEntity(userId);
        cartItemRepository.deleteAllByCart(cart);
        return buildCartResponse(cart);
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
        response.setUnitPrice(item.getUnitPrice());
        response.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        
        // Get product name from ProductService
        try {
            ProductInfo productInfo = productServiceClient.getProductById(item.getProductId());
            response.setProductName(productInfo.getName());
        } catch (Exception e) {
            response.setProductName("Product not available");
        }

        return response;
    }
} 