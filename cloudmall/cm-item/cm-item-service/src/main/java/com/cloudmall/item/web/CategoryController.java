package com.cloudmall.item.web;


import com.cloudmall.item.pojo.Category;
import com.cloudmall.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*用Reponsebody返回的是默认的json格式的响应体，但是请求 有请求头，请求行，和请求体，返回也有返回头，返回行，返回体
    * 所以我们应该用ResponseEntity<T>,包含返回头，返回行，泛型T就是返回体
    * status是状态码
    * .body是响应体
    * */
    /**
     * 根据父节点id查询商品分类
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam("pid")Long pid){
        //ResponseEntity.ok(null);//简写方式
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.queryCategoryListByPid(pid));
    }

    /**
     * 根据cid查询商品分类的接口
     * @param ids
     * @return
     */
    @GetMapping("list/ids")//请求时RequestParam的ids就是list/ids?ids=1,276,3
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }
}
