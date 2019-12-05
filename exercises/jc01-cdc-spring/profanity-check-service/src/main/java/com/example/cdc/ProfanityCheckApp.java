package com.example.cdc;

import com.example.cdc.profanity.IsSwearWord;
import com.example.cdc.profanity.PurgoMalumProfanityClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class ProfanityCheckApp {

    @Autowired
    PurgoMalumProfanityClient client;

    @RequestMapping(value = "/checkprofanity", produces = MediaType.APPLICATION_JSON_VALUE)
    public IsSwearWord check(@RequestParam("text") String input) {
        return client.profanityCheck(input);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ProfanityCheckApp.class, args);
    }

}
