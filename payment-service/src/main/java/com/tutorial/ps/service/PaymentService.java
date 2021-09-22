package com.tutorial.ps.service;

import com.tutorial.ps.entity.Payment;
import com.tutorial.ps.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment doPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
}
