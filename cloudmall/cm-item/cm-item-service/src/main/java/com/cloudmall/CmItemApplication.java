package com.cloudmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient//注册到eureka当中
@SpringBootApplication
public class CmItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmItemApplication.class);
    }
}
