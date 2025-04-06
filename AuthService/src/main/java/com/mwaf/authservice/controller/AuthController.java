package com.mwaf.authservice.controller;

import com.mwaf.authservice.Dto.AuthResponse;
import com.mwaf.authservice.Dto.CreateCustomerRequest;
import com.mwaf.authservice.Dto.LoginRequest;
import com.mwaf.authservice.Dto.RegisterRequest;
import com.mwaf.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${customer.service.url}")
    private String customerServiceUrl;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        CreateCustomerRequest registeredUser = authService.registerUserAndCreatingCostumer(registerRequest);

        try {
            String customerEndpoint = customerServiceUrl + "/api/customers";
            restTemplate.postForEntity(customerEndpoint, registeredUser, Void.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest registerRequest) {
        String result = authService.registerAdmin(registerRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * Login endpoint.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = authService.loginUser(loginRequest);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
