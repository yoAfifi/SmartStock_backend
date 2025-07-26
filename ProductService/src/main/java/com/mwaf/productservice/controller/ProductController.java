package com.mwaf.productservice.controller;


import com.mwaf.productservice.model.Product;
import com.mwaf.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // Constructor injection for ProductService
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create a new product
//    @PostMapping
//    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
//        Product createdProduct = productService.createProduct(product);
//        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
//    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> create(
            @RequestPart("product") Product product,
            @RequestPart("file") MultipartFile file) throws IOException {

        Product saved = productService.createProduct(product, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Retrieve a product by its ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // Retrieve all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Update an existing product
//    @PutMapping("/{id}")
//    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
//        Product updatedProduct = productService.updateProduct(id, product);
//        return ResponseEntity.ok(updatedProduct);
//    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> update(@PathVariable Long id,
                                          @RequestPart("product") Product product,
                                          @RequestPart(value = "file", required = false) MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(productService.update(id, product, file));
    }

    // Delete a product by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reduceStock")
    public ResponseEntity<Void> reduceStock(@PathVariable Long id, @RequestParam int quantity) {
        productService.reduceStock(id, quantity);
        return ResponseEntity.ok().build();
    }
}