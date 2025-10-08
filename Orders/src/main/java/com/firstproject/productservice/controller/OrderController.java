package com.firstproject.productservice.controller;

import com.firstproject.productservice.model.Orders;
import com.firstproject.productservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    // Place an order
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = ((Number) payload.get("userId")).longValue();
            Long productId = ((Number) payload.get("productId")).longValue();
            Integer quantity = ((Number) payload.get("quantity")).intValue();

            // 1. Fetch product details
            String prodUrl = String.format("http://PRODUCTSERVICE/products?prodId=%d", productId);
            Map<?,?> product = restTemplate.getForObject(prodUrl, Map.class);
            if (product == null || product.isEmpty()) {
                return ResponseEntity.badRequest().body("Product not found");
            }

            // 2. Fetch user details
            String userUrl = String.format("http://USER-SERVICE/user/%d", userId);
            Map<?,?> user = restTemplate.getForObject(userUrl, Map.class);
            if (user == null || user.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            // 3. Reduce stock (PUT)
            String reduceUrl = String.format("http://PRODUCTSERVICE/products/%d/reduceStock?quantity=%d", productId, quantity);
            ResponseEntity<Map> prodResp = restTemplate.exchange(reduceUrl, HttpMethod.PUT, null, Map.class);
            if (!prodResp.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough stock");
            }

            // 4. Save order snapshot
            Orders order = new Orders();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setQuantity(quantity);
            order.setProductName(String.valueOf(product.get("name")));
            order.setProductPrice(Double.parseDouble(product.get("price").toString()));
            order.setUserName(String.valueOf(user.get("name")));
            order.setUserEmail(String.valueOf(user.get("email")));
            order.setCreatedAt(LocalDateTime.now());
            order.setStatus("PLACED");

            Orders saved = orderRepository.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error placing order: " + ex.getMessage());
        }
    }

    // Cancel order -> restore inventory and change status
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        Optional<Orders> optOrder = orderRepository.findById(orderId);

        if (optOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Orders order = optOrder.get();

        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            return ResponseEntity.badRequest().body("Order already cancelled");
        }

        // call product service to restore stock
        String incUrl = String.format("http://PRODUCTSERVICE/products/%d/increaseStock?quantity=%d", 
                                      order.getProductId(), order.getQuantity());
        ResponseEntity<Map> resp = restTemplate.exchange(incUrl, HttpMethod.PUT, null, Map.class);

        if (!resp.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not restore stock");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
        return ResponseEntity.ok(order);
    }


    @GetMapping
    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }
}
