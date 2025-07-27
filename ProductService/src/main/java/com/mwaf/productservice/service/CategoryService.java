package com.mwaf.productservice.service;

import com.mwaf.productservice.model.Category;
import com.mwaf.productservice.repository.CategoryRepository;
import com.mwaf.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Transactional
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category with name '" + category.getName() + "' already exists");
        }
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new RuntimeException("Category with slug '" + category.getSlug() + "' already exists");
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category existingCategory = getCategoryById(id);
        
        // Check if name is being changed and if it conflicts with existing
        if (!existingCategory.getName().equals(categoryDetails.getName()) && 
            categoryRepository.existsByName(categoryDetails.getName())) {
            throw new RuntimeException("Category with name '" + categoryDetails.getName() + "' already exists");
        }
        
        // Check if slug is being changed and if it conflicts with existing
        if (!existingCategory.getSlug().equals(categoryDetails.getSlug()) && 
            categoryRepository.existsBySlug(categoryDetails.getSlug())) {
            throw new RuntimeException("Category with slug '" + categoryDetails.getSlug() + "' already exists");
        }
        
        existingCategory.setName(categoryDetails.getName());
        existingCategory.setSlug(categoryDetails.getSlug());
        
        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        
        // Check if category is referenced by any products
        long productCount = productRepository.countByCategory(category);
        if (productCount > 0) {
            throw new RuntimeException("Cannot delete category. It is referenced by " + productCount + " product(s)");
        }
        
        categoryRepository.delete(category);
    }
} 