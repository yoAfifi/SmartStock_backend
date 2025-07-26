package com.mwaf.authservice.service;

import com.mwaf.authservice.Dto.CreateCustomerRequest;
import com.mwaf.authservice.Dto.LoginRequest;
import com.mwaf.authservice.Dto.RegisterRequest;
import com.mwaf.authservice.Dto.CustomerDto;
import com.mwaf.authservice.model.Role;
import com.mwaf.authservice.model.User;
import com.mwaf.authservice.repository.RoleRepository;
import com.mwaf.authservice.repository.UserRepository;
import com.mwaf.authservice.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.registration.token}")
    private String adminRegistrationToken;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${customer.service.url}")
    private String customerServiceUrl;


    public CreateCustomerRequest registerUserAndCreatingCostumer(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Optional<Role> roleOpt = roleRepository.findByName("ROLE_USER");
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("Default user role not found");
        }

        HashSet<Role> roles = new HashSet<>();
        roles.add(roleOpt.get());
        user.setRoles(roles);

        userRepository.save(user);

        // Prepare the DTO to be sent to the CustomerService.
        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setUserId(user.getId());
        createCustomerRequest.setName(registerRequest.getName());
        createCustomerRequest.setEmail(registerRequest.getEmail());
        createCustomerRequest.setPhone(registerRequest.getPhone());
        createCustomerRequest.setAddress(registerRequest.getAddress());

        return createCustomerRequest;
    }

    /**
     * Registers a new admin account.
     * This endpoint requires the client to send a valid admin token in the RegisterRequest.
     */
    public String registerAdmin(RegisterRequest registerRequest) {

         
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Verify the admin token.
        if (registerRequest.getAdminToken() == null ||
                !registerRequest.getAdminToken().equals(adminRegistrationToken)) {
            throw new RuntimeException("Invalid admin token. Not authorized to create admin account.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Optional<Role> roleOpt = roleRepository.findByName("ROLE_ADMIN");
        if (!roleOpt.isPresent()) {
            throw new RuntimeException("Admin role not found");
        }

        HashSet<Role> roles = new HashSet<>();
        roles.add(roleOpt.get());
        user.setRoles(roles);

        userRepository.save(user);

        return "Admin registered successfully";
    }


    public String loginUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Auth user ID
        Long authUserId = user.getId();

        // Check if this user is an admin
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        // If admin, skip calling the Customer Service
        Long customerId = null;
        if (!isAdmin) {
            String customerEndpoint = customerServiceUrl + "/api/customers/byUserId/" + authUserId;
            CustomerDto customerDto = restTemplate.getForObject(customerEndpoint, CustomerDto.class);
            if (customerDto != null) {
                customerId = customerDto.getId();
            }
        }

        // Build JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));
        claims.put("authUserId", authUserId);
        claims.put("customerId", customerId);

        // Generate the token with subject=authUserId
        return jwtUtil.generateToken(String.valueOf(authUserId), claims);
    }

}
