package com.mwaf.cartservice.repository;

import com.mwaf.cartservice.model.Cart;
import com.mwaf.cartservice.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
    void deleteAllByCart(Cart cart);
    
    // Direct SQL query to delete cart items by user ID
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
} 