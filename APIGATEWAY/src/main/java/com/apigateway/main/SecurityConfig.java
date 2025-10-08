package com.apigateway.main;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import org.springframework.security.web.server.WebFilterExchange;

import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
	    http
	        .authorizeExchange(exchanges -> exchanges
	            .pathMatchers("/fallback/**").permitAll()
	            .anyExchange().authenticated()
	        )
	        .oauth2Login(oauth2 -> oauth2
	            .authenticationSuccessHandler((webFilterExchange, authentication) -> {
	                // Force redirect to /productservice/products after login
	                webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
	                webFilterExchange.getExchange().getResponse()
	                    .getHeaders()
	                    .setLocation(URI.create("/productservice/products"));
	                return webFilterExchange.getExchange().getResponse().setComplete();
	            })
	        )
	        .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt)
	        .csrf(csrf -> csrf.disable());

	    return http.build();
	}
}
