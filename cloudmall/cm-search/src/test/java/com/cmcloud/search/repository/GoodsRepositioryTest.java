package com.cmcloud.search.repository;

import com.cloudmall.common.vo.PageResult;
import com.cloudmall.item.pojo.Spu;
import com.cloudmall.item.vo.SpuVo;
import com.cmcloud.search.client.GoodsClient;
import com.cmcloud.search.pojo.Goods;
import com.cmcloud.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositioryTest {
    @Autowired
    private  GoodsRepositiory goodsRepositiory;

    @Autowired
    private ElasticsearchTemplate tempalte;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;
    @Test
    public void testCreateIndex(){

        tempalte.createIndex(Goods.class);
        tempalte.putMapping(Goods.class);
    }


    @Test
    public void loadData(){
        int page=1;//从第一页开始
        int rows=10;//一次查询100条
        int size=0;
        do {
            // 查询spu信息
            PageResult<SpuVo> spuVoPageResult = goodsClient.querySpuByPage(page, rows, null, true, null);
            //构建成goods
            List<SpuVo> spuVoList = spuVoPageResult.getItems();
            //如果spuVoPageResult为空就跳过
            if(CollectionUtils.isEmpty(spuVoList)){
                break;
            }
            List<Goods> goodsList = new ArrayList<>();
            for (SpuVo item : spuVoList) {
                Goods goods = searchService.buildGoods(item);
                goodsList.add(goods);
                System.out.println(item.toString());
            }

            //存入索引库
            goodsRepositiory.saveAll(goodsList);
/*            for (Goods goods : goodsList) {
                System.out.println(goods);
            }*/
            //翻页
            page++;
            size=spuVoList.size();
            //如果查满一百条，说明还有数据，也可以从resultPage中获取总页数来判断
        }while(size==10);
    }
}