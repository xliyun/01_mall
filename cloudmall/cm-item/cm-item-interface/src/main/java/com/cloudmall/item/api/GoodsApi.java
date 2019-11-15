package com.cloudmall.item.api;

import com.cloudmall.common.vo.PageResult;
import com.cloudmall.item.pojo.Sku;
import com.cloudmall.item.pojo.Spu;
import com.cloudmall.item.pojo.SpuDetail;
import com.cloudmall.item.vo.SpuVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 导入spring-webmvc
 * 接口应该由提供方来提供，
 * 由cm-search maven导入后，去调用cm-item-sevice
 * 但是item-service也继承了item-interface
 * 两个方案：
 *     1.将item-interface分为api和pojo这些，item-sevice只引入api,cm-search引入api
 *     2.cm-search引入GoodsApi,然后加上@Feign注解
 */

public interface GoodsApi {

    /**
     * 根据spu的id查询详情detail
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable("id") Long spuId);

    /**
     * 根据spu查询下面的所有的sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId);

    /**
     * 分页查询SPU
     * @param page
     * @param rows
     * @param sortBy
     * @param saleable
     * @param key
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<SpuVo> querySpuByPage(
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="rows",defaultValue = "5") Integer rows,
            @RequestParam(value="SortBy",required = false) String sortBy,
            @RequestParam(value = "saleable",required = false) Boolean saleable,
            @RequestParam(value="key",required = false) String key

    );

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);
}
