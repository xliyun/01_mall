package com.cmcloud.search.client;

import com.cloudmall.item.api.CategoryApi;
import com.cloudmall.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 查询分类
 */
@FeignClient("item-service")//feign注解加微服务名称就可以发起请求了
public interface CategoryClient extends CategoryApi {

}
