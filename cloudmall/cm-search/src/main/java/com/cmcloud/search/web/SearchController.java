package com.cmcloud.search.web;

import com.cloudmall.common.vo.PageResult;
import com.cmcloud.search.pojo.Goods;
import com.cmcloud.search.pojo.SearchRequest;
import com.cmcloud.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {


    @Autowired
    private SearchService searchService;

    /**
     * @Author xiaoliyu
     * @Description
     * @Date 10:34 2019/3/24
     * @Param [request]
     * @return org.springframework.http.ResponseEntity<com.cloudmall.common.vo.PageResult<com.cmcloud.search.pojo.Goods>>
     **/
    @PostMapping("page")
    private ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){
        return ResponseEntity.ok(searchService.search(request));
    }
}
