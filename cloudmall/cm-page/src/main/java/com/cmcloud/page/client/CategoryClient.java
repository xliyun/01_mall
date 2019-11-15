package com.cmcloud.page.client;

import com.cloudmall.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 查询分类
 */
@FeignClient("item-service")//feign注解加微服务名称就可以发起请求了
public interface CategoryClient extends CategoryApi {

}
