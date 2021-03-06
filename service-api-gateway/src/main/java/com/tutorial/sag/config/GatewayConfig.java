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
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpConnectTimeoutException;
import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator buildRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order-service", p -> p.path("/orders/**")
                        .filters(f ->
                                f.circuitBreaker(config -> config.setName("CircuitBreaker")
                                        .setFallbackUri("/inCaseOfFailureUseThis")))
                        .uri("lb://order-service"))
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder("CircuitBreaker")
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                        .slidingWindowSize(5)
                        .minimumNumberOfCalls(3)
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(2))
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .recordExceptions(ConnectException.class, IllegalStateException.class,
                                IOException.class, HttpConnectTimeoutException.class, HttpServerErrorException.class)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofMillis(200))
                        .build())
                .build());
    }
}
