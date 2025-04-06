package com.mwaf.orderservice.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class Customer{
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private String address;

    // Getters and setters...
}