package com.cloudmall.gateway.filters;

import com.cloudmall.common.utils.CookieUtils;
import com.cloudmall.gateway.config.FilterProperties;
import com.cloudmall.gateway.config.JwtProperties;
import com.cmcloud.auth.UserInfo;
import com.cmcloud.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-18 17:53
 */
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class AuthFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        //返回过滤器类型 前置、后置 、路由、异常过滤器
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //返回过滤器顺序 5是官方过滤器顺序，我们的过滤器放到它们前面
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();

        //获取请求的url路径
        String requestUrl = request.getRequestURI();

        //String method = request.getMethod();

        //判断是否放行，拦截则返回false
        //return !isAllowPath(requestUrl);//如果是白名单里的路径，就放行，放行是返回false 不走run
        return false;
    }

    private boolean isAllowPath(String requestUrl) {
        // TODO 这里直接放行
        //遍历白名单
        for (String allowPath : filterProperties.getAllowPaths()) {
            //判断是否 是允许的
            if(requestUrl.startsWith(allowPath))
                return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {

        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = ctx.getRequest();
        //获取cookie中的token
        //Cookie[] cookies = request.getCookies();
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        try {
            //解析token
            UserInfo user = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            // TODO 验证权限

        } catch (Exception e) {
            //解析token失败，未登录,拦截
            ctx.setSendZuulResponse(false);
            //返回状态码 403代表未授权
            ctx.setResponseStatusCode(403);
        }

        return null;
    }
}
