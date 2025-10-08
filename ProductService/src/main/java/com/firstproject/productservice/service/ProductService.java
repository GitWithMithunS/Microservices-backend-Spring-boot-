package com.firstproject.productservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import com.firstproject.productservice.model.Product;
import com.firstproject.productservice.repo.ProductRepository;

import java.util.List;
import java.util.Optional;
 
@Service
public class ProductService {
 
    @Autowired
    private ProductRepository productRepository;
 
    // Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
 
    // Get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
 
    // Add product
    public Product addProduct(Product prod) {
        return productRepository.save(prod);
    }
 
    // Delete product by ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
 

