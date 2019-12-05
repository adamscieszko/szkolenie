package com.example.discovery.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class Greeter {

    private static Logger log = LoggerFactory.getLogger(Greeter.class);

    @Value("${message:Hello default!}")
    private String message;

    @RequestMapping("/greeting")
    Greeting getMessage() {
        log.info("Welcome message: {}", message);
        return new Greeting(message);
    }
}
