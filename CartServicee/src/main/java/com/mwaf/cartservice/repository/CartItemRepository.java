package com.mwaf.cartservice.repository;

import com.mwaf.cartservice.model.Cart;
import com.mwaf.cartservice.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
    void deleteAllByCart(Cart cart);
} 