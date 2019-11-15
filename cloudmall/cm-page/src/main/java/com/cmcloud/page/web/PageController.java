package com.cmcloud.page.web;

import com.cmcloud.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-03 10:22
 */
@Controller//不用RestController因为它会把返回结果转换为json
public class PageController {
    @Autowired
    private PageService pageService;
    @GetMapping("itemdt/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model){
        //查询模型数据
        Map<String,Object> attributes=pageService.loadModel(spuId);
        //准备模型数据
        model.addAllAttributes(attributes);
        //model.addAttribute();
        return "item";
    }
}
