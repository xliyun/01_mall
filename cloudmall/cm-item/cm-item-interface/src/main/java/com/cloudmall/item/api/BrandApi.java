package com.cloudmall.item.api;

import com.cloudmall.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface BrandApi {
    /**
     *  根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("brand/{id}")//嫌分开写麻烦就在类头上加RequestMapping("brand")
    Brand queryBrandById(@PathVariable("id")Long id);

    @GetMapping("brand/brands")
    List<Brand> queryBrandByIds(@RequestParam("ids")List<Long> ids);
}
