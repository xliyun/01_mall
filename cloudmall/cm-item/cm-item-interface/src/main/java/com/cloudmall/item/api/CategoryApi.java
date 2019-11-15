package com.cloudmall.item.api;

import com.cloudmall.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi {
    //参考categoryController写，不需要加ResponseEntity,就不需要判断ResponseEntity里面的状态码是什么
    //ResponseEntity返回的不是结果，只是一个标记，就是告诉springMVC，应该写到响应体里去，响应体里的内容是List<>不是ResponseEntity
    //如果有ResponseEntity，不管响应码是多少都会成功，去掉后只有200-299的响应码才成功
    @GetMapping("category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam("ids")List<Long> ids);
}
