package com.tutorial.os.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.os.dto.Payment;
import com.tutorial.os.dto.TransactionRequest;
import com.tutorial.os.dto.TransactionResponse;
import com.tutorial.os.entity.Order;
import com.tutorial.os.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    @Value("${microservice.payment-service.endpoints.endpoint.uri}")
    private String PAYMENT_URL;

    public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
        String message = "";
        Payment payment = request.getPayment();
        payment.setOrderId(request.getOrder().getId());
        payment.setAmount(request.getOrder().getPrice());

        log.info("Order service request {}", new ObjectMapper().writeValueAsString(request));
        // rest call to payment service
        Payment paymentResponse = restTemplate.postForObject(PAYMENT_URL, payment, Payment.class);
        log.info("Payment service response {}", new ObjectMapper().writeValueAsString(paymentResponse));
        Order order = request.getOrder();
        if("success".equals(paymentResponse.getPaymentStatus())) {
            orderRepository.save(order);
            message = "order placed successfully";
        } else {
            message = "error while placing order";
        }
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(), message);
    }

    public Order findOrderDetailsById(int orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}
