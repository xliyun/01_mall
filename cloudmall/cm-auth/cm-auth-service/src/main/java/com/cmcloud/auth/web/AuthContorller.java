package com.cmcloud.auth.web;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.common.utils.CookieUtils;
import com.cmcloud.auth.UserInfo;
import com.cmcloud.auth.config.JwtProperties;
import com.cmcloud.auth.service.AuthService;
import com.cmcloud.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-16 17:32
 */
@Slf4j
@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthContorller {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    private static final Logger logger = LoggerFactory.getLogger(AuthContorller.class);

  /*  @Value("${cmcloud.jwt.cookieName}")
    private  String cookieName;*/
    /**
     * @Author xiaoliyu
     * @Description //登陆授权
     * @Date 17:54 2019/4/16
     **/
    @PostMapping("login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password")String password,
                                      HttpServletRequest request,
                                      HttpServletResponse response){

        //登录
        String token=authService.login(username,password);
        if (StringUtils.isBlank(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //写入cookie
        Cookie cookie = new Cookie("CM_TOKEN", token);
        //2.将token写入cookie，并指定httpOnly为true，防止通过js获取和修改
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge(),true);
        //TODO 构建器写法 charset方法是要不要进行编码，如果有中文就设置一下 maxAge cookie最大生命时间 httpOnly为true禁止js操纵你的cookie,避免跨站攻击
        //需要request是因为cookie有域的概念domian，给cookie设置域，防止其他网站访问
        //CookieUtils.newBuilder(response).httpOnly().request(request).build("CM_TOKEN",token);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /*
     * @Author xiaoliyu
     * @Description //每当用户访问新的页面，都会刷新token
     * @Date 14:54 2019/4/18
     **/
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("CM_TOKEN")String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response){
        try {
            // 解析token
            UserInfo info= JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
            //刷新token,这样 每次验证用户的时候后延长token时间
            String newToken = JwtUtils.generateToken(info, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
            //将新cookie重新 写入token
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge(),true);
            //已登录，返回用户信息
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            //token已过期，或者token被篡改
            logger.error("[登陆] token已过期，或者token被篡改");
            throw new CmException(ExceptionEnum.INVALID_TOKEN);
        }
    }
}
