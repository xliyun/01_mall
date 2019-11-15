package com.cmcloud.page.client;

import com.cloudmall.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface BrandClient extends BrandApi {
}
