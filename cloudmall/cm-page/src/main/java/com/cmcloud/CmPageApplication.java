package com.cmcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-02 15:37
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//解除自动加载DataSourceAutoConfiguration，就是不用连接数据库，不然报Failed to configure a DataSource
public class CmPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmPageApplication.class);
    }
}
