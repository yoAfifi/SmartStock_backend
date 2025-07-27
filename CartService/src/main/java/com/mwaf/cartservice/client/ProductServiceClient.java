package com.mwaf.cartservice.client;

import com.mwaf.cartservice.dto.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ProductService")
public interface ProductServiceClient {

    @GetMapping("/api/products/{id}/info")
    ProductInfo getProductById(@PathVariable("id") Long id);
} 