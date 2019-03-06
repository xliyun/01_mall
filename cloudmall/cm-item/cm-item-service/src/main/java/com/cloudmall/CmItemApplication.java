package com.cloudmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient//注册到eureka当中，这里不推荐用EnableEurekaClient因为springcloud内置注册中心不止eureka一种,zookeeper
@SpringBootApplication
@MapperScan("com.cloudmall.item.mapper")
public class CmItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmItemApplication.class);
    }
}
