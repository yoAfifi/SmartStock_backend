package com.mwaf.productservice.service;

import com.mwaf.productservice.model.Product;
import com.mwaf.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    // Constructor injection for ProductRepository
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create a new product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // Retrieve a product by its ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // Retrieve all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Update an existing product
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStockQuantity(productDetails.getStockQuantity());
        return productRepository.save(existingProduct);
    }

    // Delete a product by its ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void reduceStock(Long productId, int quantity) {
        // 1. Retrieve the product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // 2. Check if there's enough stock
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId);
        }

        // 3. Reduce the stock
        product.setStockQuantity(product.getStockQuantity() - quantity);
        if (product.getStockQuantity() == 0) {
            productRepository.delete(product);
        } else {
            productRepository.save(product);
        }


    }


}
