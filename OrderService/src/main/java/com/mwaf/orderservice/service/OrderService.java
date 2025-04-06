package com.mwaf.orderservice.service;

import com.mwaf.orderservice.dto.ProductDTO;
import com.mwaf.orderservice.model.Order;
import com.mwaf.orderservice.model.OrderItem;
import com.mwaf.orderservice.client.ProductServiceClient;

import com.mwaf.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;


    // Constructor injection for OrderRepository
    public OrderService(OrderRepository orderRepository ,
                        ProductServiceClient productServiceClient   ) {
        this.orderRepository = orderRepository;
        this.productServiceClient = productServiceClient;
    }

   // make sure to import this at the top

    public Order createOrder(Order order) {
        // If orderDate is not provided, set it to the current date/time
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        // Optionally, if status is required and not provided, set a default value (e.g., "NEW")
        if (order.getStatus() == null) {
            order.setStatus("NEW");
        }

        // We'll accumulate total cost here
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {
            // 1. Fetch product info from Product Service
            ProductDTO productDTO = productServiceClient.getProductById(item.getProductId());

            // 2. Check if the requested quantity is available
            if (productDTO.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient stock for product ID: " + item.getProductId() +
                                ". Available: " + productDTO.getStockQuantity() +
                                ", Requested: " + item.getQuantity()
                );
            }

            // 3. Calculate cost for this item (product’s price * quantity)
            BigDecimal itemCost = productDTO.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemCost);

            // (Optional) Store the product’s price in the OrderItem so you have a snapshot of the price at ordering time
            item.setUnitPrice(productDTO.getPrice());
        }

        // Reduce stock for each order item
        for (OrderItem item : order.getOrderItems()) {
            productServiceClient.reduceStock(item.getProductId(), item.getQuantity());
        }

        // 4. Set the total amount on the order
        order.setTotalAmount(totalAmount);

        // 5. Set the order reference for each order item
        for (OrderItem item : order.getOrderItems()) {
            item.setOrder(order);
        }

        // 6. Save the order (with its items) to the repository
        return orderRepository.save(order);
    }


//    public Order createOrder(Order order) {
//        // Optional: Add any business logic here (e.g., total amount calculation)
//        return orderRepository.save(order);
//    }

    // Retrieve an order by its ID
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    // Retrieve all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Update an existing order
    public Order updateOrder(Long id, Order orderDetails) {
        Order existingOrder = getOrderById(id);

        // Only update fields that you actually want changed:
        if (orderDetails.getStatus() != null) {
            existingOrder.setStatus(orderDetails.getStatus());
        }



        return orderRepository.save(existingOrder);
    }

    // Delete an order by its ID
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}