package com.firstproject.productservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firstproject.productservice.model.Product;
import com.firstproject.productservice.service.ProductService;

 
@RestController
@RequestMapping("/products")
public class ProductController {
 
    @Autowired
    private ProductService productService;
 
    // Get all products
    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }
 
    // Get product by ID
    @GetMapping(params = "prodId")
    public Optional<Product> getProductById(@RequestParam("prodId") Long prodId) {
        return productService.getProductById(prodId);
    }
 
    // Add new product
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product prod) {
        Product savedProduct = productService.addProduct(prod);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
 
    // Delete product by ID
    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@RequestParam("prodId") Long prodId) {
        productService.deleteProduct(prodId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/reduceStock")
    public ResponseEntity<Product> reduceStock(@PathVariable Long id, @RequestParam int quantity) {
        return productService.getProductById(id)
                .map(product -> {
                    if (product.getStock() < quantity) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(product);
                    }
                    product.setStock(product.getStock() - quantity);
                    Product updated = productService.addProduct(product);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/increaseStock")
    public ResponseEntity<Product> increaseStock(@PathVariable Long id, @RequestParam int quantity) {
        return productService.getProductById(id)
                .map(product -> {
                    product.setStock(product.getStock() + quantity);
                    Product updated = productService.addProduct(product);
                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
 

