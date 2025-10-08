package com.firstproject.productservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.firstproject.productservice.model.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {}
