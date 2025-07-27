package com.mwaf.orderservice.controller;

import com.mwaf.orderservice.dto.Customer;
import com.mwaf.orderservice.dto.OrderRequest;
import com.mwaf.orderservice.model.Order;
import com.mwaf.orderservice.model.OrderItem;
import com.mwaf.orderservice.service.OrderService;
import com.mwaf.orderservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Place a new order using the customer ID from the security context
    @PostMapping("/placeOrder")
    public ResponseEntity<Order> placeOrder(@RequestBody OrderRequest orderRequest) {
        return createOrder(orderRequest);
    }

    // Create a new order (alternative endpoint)
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        // Get the authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        System.out.println("Authentication: " + authentication);
        System.out.println("Is authenticated: " + (authentication != null && authentication.isAuthenticated()));
        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
        System.out.println("Credentials: " + (authentication != null ? authentication.getCredentials() : "null"));
        System.out.println("Details: " + (authentication != null ? authentication.getDetails() : "null"));
        System.out.println("Authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));
        
        if (authentication != null && authentication.isAuthenticated()) {

            Long customerId = null;
            if (authentication.getDetails() instanceof Map) {
                Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
                customerId = (Long) details.get("customerId");
                System.out.println("CustomerId from details: " + customerId);
            }
            
            // If customerId is still null, try to get it from the token
            if (customerId == null && authentication.getCredentials() instanceof String) {
                String token = (String) authentication.getCredentials();
                customerId = jwtUtil.getCustomerIdFromToken(token);
                System.out.println("CustomerId from token: " + customerId);
            }
            
            if (customerId != null) {
                // Convert OrderRequest to Order entity
                Order order = new Order();
                order.setCustomerId(customerId);
                order.setStatus("NEW");
                order.setOrderDate(LocalDateTime.now());
                
                // Convert OrderItemRequest to OrderItem entities
                if (orderRequest.getOrderItems() != null) {
                    List<OrderItem> orderItems = orderRequest.getOrderItems().stream()
                        .map(itemRequest -> {
                            OrderItem item = new OrderItem();
                            item.setProductId(itemRequest.getProductId());
                            item.setQuantity(itemRequest.getQuantity());
                            item.setOrder(order);
                            return item;
                        })
                        .collect(Collectors.toList());
                    order.setOrderItems(orderItems);
                }
                
                try {
                    Order placedOrder = orderService.createOrder(order);
                    return ResponseEntity.status(HttpStatus.CREATED).body(placedOrder);
                } catch (Exception e) {
                    System.err.println("Error creating order: " + e.getMessage());
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Retrieve an order by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // Retrieve all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        // Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user has ROLE_ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        List<Order> orders;
        if (isAdmin) {
            // Admins can see all orders
            orders = orderService.getAllOrders();
        } else {
            // Regular users can only see their own orders
            String userId = authentication.getName();
            orders = orderService.getOrdersByCustomer(Long.valueOf(userId));
        }
        
        return ResponseEntity.ok(orders);
    }

    // Update an existing order
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        Order updatedOrder = orderService.updateOrder(id, order);
        return ResponseEntity.ok(updatedOrder);
    }

    // Delete an order by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/byCustomer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable Long customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }
}
