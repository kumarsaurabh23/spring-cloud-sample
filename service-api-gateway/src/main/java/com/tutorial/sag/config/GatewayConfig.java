package com.tutorial.sag.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator buildRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order-service", p -> p.path("/orders/**").uri("lb://order-service"))
                .route("payment-service",
                        p -> p.path("/payment/**")
                                .filters(f ->
                                        f.circuitBreaker(config -> config.setName("myCircuitBreaker")
                                                .setFallbackUri("/inCaseOfFailureUseThis")))
                                .uri("lb://payment-service"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(s -> new Resilience4JConfigBuilder(s)
                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofMillis(200))
                        .build())
                .build());
    }
}
