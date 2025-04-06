package com.mwaf.authservice.Dto;



import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginRequest {
    private String username;
    private String password;
}