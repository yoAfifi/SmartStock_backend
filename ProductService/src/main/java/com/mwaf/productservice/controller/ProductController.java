package com.mwaf.productservice.controller;

import com.mwaf.productservice.dto.ProductRequest;
import com.mwaf.productservice.dto.ProductInfo;
import com.mwaf.productservice.model.Product;
import com.mwaf.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
            @RequestPart("product") @Valid ProductRequest productRequest,
            @RequestPart("file") MultipartFile file) throws IOException {

        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        
        Product saved = productService.createProduct(product, file, productRequest.getCategoryId());
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
                                          @RequestPart("product") @Valid ProductRequest productRequest,
                                          @RequestPart(value = "file", required = false) MultipartFile file)
            throws IOException {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());
        
        return ResponseEntity.ok(productService.update(id, product, file, productRequest.getCategoryId()));
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

    @GetMapping("/{id}/info")
    public ResponseEntity<ProductInfo> getProductInfo(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductInfo productInfo = new ProductInfo();
        productInfo.setId(product.getId());
        productInfo.setName(product.getName());
        productInfo.setImageUrl(product.getImageUrl());
        productInfo.setPrice(product.getPrice());
        productInfo.setStockQuantity(product.getStockQuantity());
        return ResponseEntity.ok(productInfo);
    }

    // Get products by stock level
    @GetMapping("/stock/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getProductsWithStock();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/stock/low")
    public ResponseEntity<List<Product>> getLowStockProducts() {
        List<Product> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/stock/zero")
    public ResponseEntity<List<Product>> getZeroStockProducts() {
        List<Product> products = productService.getZeroStockProducts();
        return ResponseEntity.ok(products);
    }

    // Bulk operations for zero stock products
    @PostMapping("/stock/zero/bulk-delete")
    public ResponseEntity<Void> bulkDeleteZeroStockProducts(@RequestBody List<Long> productIds) {
        productService.bulkDeleteProducts(productIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/stock/zero/bulk-restock")
    public ResponseEntity<Void> bulkRestockProducts(@RequestBody Map<Long, Integer> restockData) {
        productService.bulkRestockProducts(restockData);
        return ResponseEntity.ok().build();
    }
}