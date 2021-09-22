package com.tutorial.os.controller;

import com.tutorial.os.entity.Order;
import com.tutorial.os.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(path = "/bookOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    public Order bookOrder(@RequestBody Order order) {
        return orderService.saveOrder(order);
    }
}
