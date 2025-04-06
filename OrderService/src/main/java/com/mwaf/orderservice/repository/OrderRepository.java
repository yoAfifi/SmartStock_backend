package com.mwaf.orderservice.repository;

import com.mwaf.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can add custom query methods if needed. }
    List<Order> findByCustomerId(Long customerId);
}