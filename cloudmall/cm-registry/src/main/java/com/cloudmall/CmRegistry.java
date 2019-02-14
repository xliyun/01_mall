package com.cloudmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class CmRegistry {
    public static void main(String[] args) {
        SpringApplication.run(CmRegistry.class);
    }
}
