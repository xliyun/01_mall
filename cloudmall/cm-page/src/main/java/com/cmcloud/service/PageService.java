package com.cmcloud.service;

import com.cloudmall.common.utils.JsonUtils;
import com.cloudmall.item.pojo.*;
import com.cmcloud.page.client.BrandClient;
import com.cmcloud.page.client.CategoryClient;
import com.cmcloud.page.client.GoodsClient;
import com.cmcloud.page.client.SpecificationClient;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * @description:
 * @author: xiaoliyu
 * @date: 2019-04-03 15:32
 */
@Slf4j
@Service
public class PageService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specClient;

    @Autowired
    private TemplateEngine templateEngine;

    public Map<String, Object> loadModel(Long spuId) {
        Map<String,Object> model=new HashMap<>();
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);

        //查询skus
        List<Sku> skus = spu.getSkus();

        //查询详情
        SpuDetail spuDetail = spu.getSpuDetail();

        //查询brand
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        //查询商品分类
        List<Category> categories = categoryClient.queryCategoryByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询规格参数
        List<SpecGroup> specGroups = specClient.queryGroupListByCid(spu.getCid3());

        List<SpecParam> params=getSpecParam(specGroups);

        Map<Long,String> paramMap = new HashMap<>();
        for (SpecParam param : params) {
            paramMap.put(param.getId(), param.getName());//4 机身颜色，5，内存
        }

        //把这个几个设置为空，给前端减减压
        spu.setSpuDetail(null);
        spu.setSkus(null);
        System.out.println(specGroups.toString());
        model.put("spu",spu);
        model.put("skus",skus);
        model.put("spuDetail",spuDetail);
        model.put("brand",brand);
        model.put("categories",categories);
        model.put("specs",specGroups);
        model.put("paramMap", paramMap);

        return model;
    }

    private List<SpecParam> getSpecParam(List<SpecGroup> specGroups) {
        List<SpecParam> specParams=new ArrayList<>();
        for (SpecGroup specGroup : specGroups) {
            for (SpecParam param : specGroup.getParams()) {
                if(!param.getGeneric())
                    specParams.add(param);
            }
        }
        return specParams;
    }
    /*thymeleaf实现静态化
    * Context上下文
    * TemplateResolver模板解析器
    * TemplateEngin模板引擎 三个参数 模板名称，上下文(包含模型数据),writer输出目的地的流
    * */

    public void createhtml(Long spuId){
        //上下文
        Context context = new Context();
        context.setVariables(loadModel(spuId));
        //输出流
        File file=new File("F:\\LOGS",spuId+".html");

        //如果文件存在就删除
        if(file.exists()){
            file.delete();
        }

        //打印流，最方便的流
        try(PrintWriter writer=new PrintWriter(file,"UTF-8")){
            //生成html
            templateEngine.process("item",context,writer);
        }catch (Exception e){
            log.error("生成静态页异常",e);
        }

    }

    public void deleteHtml(Long spuId) {
        File file=new File("F:\\LOGS",spuId+".html");
        //如果文件存在就删除
        if(file.exists()){
            file.delete();
        }
    }

}
