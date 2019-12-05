package com.example.discovery;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RibbonClient("service")
public class ClientApplication {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args)  {
        new SpringApplicationBuilder(ClientApplication.class)
                .web(false)
                .run(args);
    }

}

@Component
class Caller implements CommandLineRunner {

    private static Logger log = org.slf4j.LoggerFactory.getLogger(Caller.class);

    @Autowired
    GreetingConsumer g;

    public void run(String... args) throws Exception {
        for (;;) {
            try {
                g.saidGreeting();
                Thread.sleep(500);
            } catch (Exception e) {
                log.info("Ooppps: {}", e.getMessage());
            }
        }
    }
}

@Component
class GreetingConsumer {

    private static Logger log = org.slf4j.LoggerFactory.getLogger(GreetingConsumer.class);

    @Autowired
    RestTemplate restTemplate;

    public String saidGreeting() {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("http://service/greeting", String.class);
        final String resp = entity.getBody();
        log.info("Calling {} returning {}", entity.getHeaders().get("X-Application-Context"), resp);

        return resp;
    }

}
