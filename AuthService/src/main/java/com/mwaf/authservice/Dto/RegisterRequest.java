package com.mwaf.authservice.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RegisterRequest {

    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;


    private String role;

    private String adminToken;
}