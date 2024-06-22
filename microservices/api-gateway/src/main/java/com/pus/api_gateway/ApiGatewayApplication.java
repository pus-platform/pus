package com.pus.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()

				.route(p -> p

						.path("/users/**").filters(f -> f
								.circuitBreaker(config -> config
										.setFallbackUri("forward:/fallback")))
						.uri("lb://user-service"))

				.route(p -> p

						.path("/auth/**").filters(f -> f
								.circuitBreaker(config -> config
										.setFallbackUri("forward:/fallback")))
						.uri("lb://user-service"))

				.route(p -> p

						.path("/posts/**").filters(f -> f
								.circuitBreaker(config -> config
										.setFallbackUri("forward:/fallback")))
						.uri("lb://post-service"))
				.route(p -> p

						.path("/bookmarks/**").filters(f -> f
								.circuitBreaker(config -> config
										.setFallbackUri("forward:/fallback")))
						.uri("lb://post-service"))

				.route(p -> p

						.path("/uploads/**").filters(f -> f
								.circuitBreaker(config -> config
										.setFallbackUri("forward:/fallback")))
						.uri("lb://upload-service"))

				.route(p -> p

						.path("/stories/**").filters(f -> f
								.circuitBreaker(config -> config
										.setFallbackUri("forward:/fallback")))
						.uri("lb://story-service"))

				.build();
	}
}
