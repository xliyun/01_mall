package com.cmcloud.page.client;

import com.cloudmall.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 继承自ietm-interface的GoodsApi
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

}
