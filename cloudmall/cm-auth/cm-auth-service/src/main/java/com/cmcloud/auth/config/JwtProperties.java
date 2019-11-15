package com.cmcloud.auth.config;

import com.cloudmall.common.utils.JsonUtils;
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
    private String secret;

    private String pubKeyPath;

    private String priKeyPath;

    private int expire;

    private PublicKey publicKey;//公钥

    private PrivateKey privateKey;//私钥

    private Integer cookieMaxAge;

    private String cookieName;


    //对象一旦实例化完成以后，就应该读取公钥和私钥到内存
    //PostConstruct注解在构造函数执行完毕以后执行
    @PostConstruct
    public void init(){
        try {
            //公钥私钥不存在，先生成公钥私钥
            File pubPath = new File(pubKeyPath);
            File priPath = new File(priKeyPath);
            if(!pubPath.exists() || !priPath.exists()){
                //生成公钥私钥
                RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
            }
            // 读取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
            this.privateKey= RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥和私钥失败!",e);
            throw new RuntimeException();
        }
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
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

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public Integer getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}
