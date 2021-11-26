package com.tutorial.ps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.ps.entity.Payment;
import com.tutorial.ps.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment doPayment(Payment payment) throws JsonProcessingException {
        payment.setPaymentStatus(getPaymentProcessingStatus());
        payment.setTransactionId(UUID.randomUUID().toString());
        log.info("Payment service request {}", new ObjectMapper().writeValueAsString(payment));
        return paymentRepository.save(payment);
    }

    private String getPaymentProcessingStatus() {
        return new Random().nextBoolean() ? "success" : "false";
    }
}
