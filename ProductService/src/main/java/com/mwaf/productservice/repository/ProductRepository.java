package com.mwaf.productservice.repository;

import com.mwaf.productservice.model.Category;
import com.mwaf.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository 
public interface ProductRepository extends JpaRepository<Product, Long> {
    long countByCategory(Category category);
    
    // Stock management queries
    List<Product> findByStockQuantityGreaterThan(int quantity);
    List<Product> findByStockQuantityBetween(int minQuantity, int maxQuantity);
    List<Product> findByStockQuantity(int quantity);
    List<Product> findByStockQuantityLessThan(int quantity);
}
