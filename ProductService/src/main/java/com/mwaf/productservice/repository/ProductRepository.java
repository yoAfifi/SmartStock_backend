package com.mwaf.productservice.repository;



import com.mwaf.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository public interface ProductRepository extends JpaRepository<Product, Long>
{ }
