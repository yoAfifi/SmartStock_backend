package com.mwaf.orderservice.dto;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
@Getter
@Setter
public class OrderRequest {
    // List of order items details that the client will send
    private List<OrderItemRequest> orderItems;



}
