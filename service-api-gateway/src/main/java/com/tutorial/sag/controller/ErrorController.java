package com.tutorial.sag.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ErrorController {

    @RequestMapping(path = "/inCaseOfFailureUseThis", produces = MediaType.APPLICATION_JSON_VALUE)
    public String doFailure() {
        return "payment service is down";
    }
}
