package com.cloudmall.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-19 13:07
 */
@Component
@ConfigurationProperties(prefix = "cmcloud.filter")
public class FilterProperties {
    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        return allowPaths;
    }

    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }
}
