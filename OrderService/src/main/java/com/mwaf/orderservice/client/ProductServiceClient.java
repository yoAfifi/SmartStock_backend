package com.mwaf.orderservice.client;

import com.mwaf.orderservice.config.FeignConfig;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.mwaf.orderservice.dto.ProductDTO;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "product-service", url = "${product.service.url}",configuration = FeignConfig.class)
public interface ProductServiceClient {

    @PutMapping("/api/products/{id}/reduceStock")
    void reduceStock(@PathVariable("id") Long id, @RequestParam("quantity") int quantity);

    @GetMapping("/api/products/{id}")
    ProductDTO getProductById(@PathVariable("id") Long id);
}