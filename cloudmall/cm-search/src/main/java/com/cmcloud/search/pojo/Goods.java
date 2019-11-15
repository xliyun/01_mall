package com.cmcloud.search.pojo;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
    public class Goods {
        @Id
        private Long id; // spuId
        @Field(type = FieldType.Text, analyzer = "ik_max_word")
        private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌
        //Keyword 不分词，index=false不创建索引 仅仅作为展示
        @Field(type = FieldType.Keyword, index = false)
        private String subTitle;// 卖点
        private Long brandId;// 品牌id
        private Long cid1;// 1级分类id
        private Long cid2;// 2级分类id
        private Long cid3;// 3级分类id
        private Date createTime;// 创建时间 过滤字段之一
        private Set<Long> price;// 价格 过滤字段之一
        @Field(type = FieldType.Keyword, index = false)
        private String skus;// 多个sku信息的json结构，不需要搜索和过滤，只用来展示
        private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值-> {"内存":4GB}  string类型，在elasticsearch中不分词
        //{"机身内存":"4GB"} 在elasticsearch中就是spec.机身内存.keyword
        /**
         * GET /goods/_search
         * {
         *     "size":1,
         *     "aggs":{
         *         "demo":{
         *             "terms":{
         *                 "field":"spec.内存.keyword",
         *                 "size":10
         *             }
         *         }
         *     }
         * }
         **/
    }