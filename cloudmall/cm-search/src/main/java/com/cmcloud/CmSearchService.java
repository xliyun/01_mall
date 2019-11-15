package com.cmcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients //远程调用
public class CmSearchService {
    public static void main(String[] args) {
        SpringApplication.run(CmSearchService.class, args);
    }
}