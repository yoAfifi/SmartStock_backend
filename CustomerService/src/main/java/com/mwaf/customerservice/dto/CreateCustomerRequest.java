package com.mwaf.customerservice.dto;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerRequest {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String address;
} 