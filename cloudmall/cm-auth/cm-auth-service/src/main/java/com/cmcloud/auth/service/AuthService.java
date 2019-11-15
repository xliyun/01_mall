package com.cmcloud.auth.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cmcloud.auth.UserInfo;
import com.cmcloud.auth.client.UserClient;
import com.cmcloud.auth.config.JwtProperties;
import com.cmcloud.user.pojo.User;
import com.cmcloud.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-16 17:37
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public String login(String username, String password) {

            //校验用户名和密码
            User user=userClient.queryUserByUserNameAndPasswd(username,password);
            if(user==null){
                throw new CmException(ExceptionEnum.USER_NOT_EXISTS);
            }
        try {
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            return token;
        } catch (Exception e) {
            logger.error("[授权中心] 生成用户token失败,用户名称:{}",username,e);
            throw new CmException(ExceptionEnum.GENERATE_TOKEN_EROOR);
        }
    }
}
