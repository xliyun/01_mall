package com.cmcloud.page.client;

import com.cloudmall.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}
