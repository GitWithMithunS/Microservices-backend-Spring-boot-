package com.firstproject.productservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.firstproject.productservice.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}

