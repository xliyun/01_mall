package com.cmcloud.search.client;

import com.cloudmall.item.api.GoodsApi;
import com.cloudmall.item.pojo.Sku;
import com.cloudmall.item.pojo.SpuDetail;
import com.cloudmall.item.vo.SpuVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 继承自ietm-interface的GoodsApi
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {

}
