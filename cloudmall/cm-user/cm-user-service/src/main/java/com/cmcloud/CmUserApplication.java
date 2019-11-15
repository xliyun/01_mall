package com.cmcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-11 17:39
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.cmcloud.user.mapper")//通用mapper的注解
public class CmUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmUserApplication.class);
    }
}
