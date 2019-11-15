package com.cloudmall.gateway.config;

import com.cmcloud.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-16 16:40
 */

@Component
@ConfigurationProperties(prefix = "cmcloud.jwt")
public class JwtProperties {

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    private String pubKeyPath;

    private String priKeyPath;

    private PublicKey publicKey;//公钥


    private String cookieName;


    //对象一旦实例化完成以后，就应该读取公钥和私钥到内存
    //PostConstruct注解在构造函数执行完毕以后执行
    @PostConstruct
    public void init(){
        try {
            // TODO 公钥
            File pubPath = new File(pubKeyPath);
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);

        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败!",e);
            throw new RuntimeException();
        }
    }


    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public String getPriKeyPath() {
        return priKeyPath;
    }

    public void setPriKeyPath(String priKeyPath) {
        this.priKeyPath = priKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
