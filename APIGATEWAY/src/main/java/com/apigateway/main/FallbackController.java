package com.apigateway.main;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
@RestController
public class FallbackController {
  
   @GetMapping("/fallback/products")
   public Mono<String> productServiceFallback() {
       return Mono.just("Product Service unavailable!");
   }
   
   @GetMapping("/fallback/orders")
   public Mono<String> orderServiceFallback() {
       return Mono.just("Orders service unavailable!");
   }

   @GetMapping("/fallback/users")
   public Mono<String> userServiceFallback() {
       return Mono.just("User service unavailable!");
   }

}