package com.mwaf.productservice.service;

import com.mwaf.productservice.model.Category;
import com.mwaf.productservice.model.Product;
import com.mwaf.productservice.model.ProductImage;
import com.mwaf.productservice.repository.CategoryRepository;
import com.mwaf.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);


    private final ProductRepository productRepository;
    private final ProductImageService imageService;
    private final CategoryRepository categoryRepository;
    private final AdminNotificationService notificationService;

    // Constructor injection for ProductRepository
    public ProductService(ProductRepository productRepository, ProductImageService imageService, CategoryRepository categoryRepository, AdminNotificationService notificationService) {
        this.productRepository = productRepository;
        this.imageService = imageService;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Product createProduct(Product product, MultipartFile file, Long categoryId) throws IOException {
        // Assign category if provided
        assignCategoryToProduct(product, categoryId);

        /* ---------- 1. persist product row FIRST ---------- */
        Product saved = productRepository.save(product);   // now we have saved.getId()

        /* ---------- 2. build a key that contains the DB id ---------- */
        String key = "products/" + saved.getId() + "/"        // <── folder = product-id
                + UUID.randomUUID() + "-"
                + file.getOriginalFilename().replace(" ", "_");

        /* ---------- 3. upload to MinIO ---------- */
        String imageUrl = imageService.upload(file, key);

        // 2. save entity with URL
        product.setImageUrl(imageUrl);
        Product finalProduct = productRepository.save(product);
        
        // Check for low stock notification
        logger.info("Calling notification service for product: {} with stock: {}", 
                   finalProduct.getName(), finalProduct.getStockQuantity());
        notificationService.checkLowStockAndNotify(finalProduct);
        
        return finalProduct;
    }

    private void assignCategoryToProduct(Product product, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            product.setCategory(category);
        }
    }
//    @Transactional
//    public Product addImage(Long productId, MultipartFile file) throws IOException {
//
//        Product product = getProductById(productId);   // reuse helper
//
//        String key  = "products/" + productId + "/"    // same folder
//                + UUID.randomUUID() + "-" + file.getOriginalFilename().replace(" ", "_");
//
//        String url  = imageService.upload(file, key);  // 👈 keeps old logic
//
//        ProductImage pi = new ProductImage();
//        pi.setProduct(product);
//        pi.setUrl(url);
//
//        product.getImages().add(pi);                   // cascade will persist
//        return product;                                // returns with images list populated
//    }

    // Create a new product
//    public Product createProduct(Product product) {
//        return productRepository.save(product);
//    }

    // Retrieve a product by its ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // Retrieve all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get products with available stock (for customers)
    public List<Product> getProductsWithStock() {
        return productRepository.findByStockQuantityGreaterThan(0);
    }

    // Get low stock products (1-5 items)
    public List<Product> getLowStockProducts() {
        return productRepository.findByStockQuantityBetween(1, 5);
    }

    // Get zero stock products
    public List<Product> getZeroStockProducts() {
        return productRepository.findByStockQuantity(0);
    }

    // Bulk delete products
    @Transactional
    public void bulkDeleteProducts(List<Long> productIds) {
        productRepository.deleteAllById(productIds);
    }

    // Bulk restock products
    @Transactional
    public void bulkRestockProducts(Map<Long, Integer> restockData) {
        for (Map.Entry<Long, Integer> entry : restockData.entrySet()) {
            Product product = getProductById(entry.getKey());
            product.setStockQuantity(product.getStockQuantity() + entry.getValue());
            productRepository.save(product);
        }
    }

    // Update an existing product
//    public Product updateProduct(Long id, Product productDetails) {
//        Product existingProduct = getProductById(id);
//        existingProduct.setName(productDetails.getName());
//        existingProduct.setDescription(productDetails.getDescription());
//        existingProduct.setPrice(productDetails.getPrice());
//        existingProduct.setStockQuantity(productDetails.getStockQuantity());
//        return productRepository.save(existingProduct);
//    }

    @Transactional
    public Product update(Long id,
                          Product incoming,
                          MultipartFile newFile,
                          Long categoryId) throws IOException {

        Product existing = getProductById(id);
        existing.setName(incoming.getName());
        existing.setDescription(incoming.getDescription());
        existing.setPrice(incoming.getPrice());
        existing.setStockQuantity(incoming.getStockQuantity());
        
        // Assign category if provided
        assignCategoryToProduct(existing, categoryId);

        if (newFile != null && !newFile.isEmpty()) {
            // build new key in the SAME product folder
            String key = "products/" + existing.getId() + "/"
                    + UUID.randomUUID() + "-" + newFile.getOriginalFilename().replace(" ", "_");

            String newUrl = imageService.upload(newFile, key);
            existing.setImageUrl(newUrl);
        }
        Product updatedProduct = productRepository.save(existing);
        
        // Check for low stock notification
        logger.info("Calling notification service for updated product: {} with stock: {}", 
                   updatedProduct.getName(), updatedProduct.getStockQuantity());
        notificationService.checkLowStockAndNotify(updatedProduct);
        
        return updatedProduct;
    }

    // Delete a product by its ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
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
            Product updatedProduct = productRepository.save(product);
            // Check for low stock notification
            logger.info("Calling notification service for stock reduction - product: {} with stock: {}", 
                       updatedProduct.getName(), updatedProduct.getStockQuantity());
            notificationService.checkLowStockAndNotify(updatedProduct);
        }
    }


}
