package com.cloudmall.upload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
//读取配置文件 出来警告，右击小扳手，show notifaction panel勾掉

@ConfigurationProperties(prefix = "cm.upload")
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;

    public UploadProperties() {
    }

    public UploadProperties(String baseUrl, List<String> allowTypes) {
        this.baseUrl = baseUrl;
        this.allowTypes = allowTypes;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<String> getAllowTypes() {
        return allowTypes;
    }

    public void setAllowTypes(List<String> allowTypes) {
        this.allowTypes = allowTypes;
    }
}
