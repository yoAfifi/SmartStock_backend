package com.mwaf.customerservice.controller;




import java.util.List; import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mwaf.customerservice.model.Customer;
import com.mwaf.customerservice.service.CustomerService;
import com.mwaf.customerservice.dto.CreateCustomerRequest;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public Customer createCustomer(@RequestBody CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setUserId(request.getUserId());
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        return customerService.createCustomer(customer);
    }

    @GetMapping("/{id}")
    public Optional<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    // New endpoint to retrieve a customer by userId
    @GetMapping("/byUserId/{userId}")
    public Customer getCustomerByUserId(@PathVariable Long userId) {
        return customerService.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found with userId: " + userId));
    }
}