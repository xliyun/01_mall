package com.cmcloud.search.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.common.utils.JsonUtils;
import com.cloudmall.common.vo.PageResult;
import com.cloudmall.item.pojo.*;
import com.cmcloud.search.client.BrandClient;
import com.cmcloud.search.client.CategoryClient;
import com.cmcloud.search.client.GoodsClient;
import com.cmcloud.search.client.SpecificationClient;
import com.cmcloud.search.pojo.Goods;
import com.cmcloud.search.pojo.SearchRequest;
import com.cmcloud.search.pojo.SearchResult;
import com.cmcloud.search.repository.GoodsRepositiory;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepositiory repositiory;

    @Autowired
    private ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu){
        Long spuId = spu.getId();
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        if(CollectionUtils.isEmpty(categories)){
            throw  new CmException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new CmException(ExceptionEnum.BRAND_NOT_FOUND);
        }

        //搜索字段  StringUtils导入的是org.apache.commons.lang3.StringUtils;
        String all=spu.getTitle()+ StringUtils.join(names," ")+brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spuId);
        if(CollectionUtils.isEmpty(skuList)){
            throw new CmException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //对sku进行处理
        List<Map<String,Object>> skus=new ArrayList<>();
        //取sku里的价格集合
        Set<Long> priceSet=new HashSet<>();
        for (Sku sku : skuList) {
            Map<String,Object> map=new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //StringUtils会先判断是否为空
            map.put("images",StringUtils.substringBefore(sku.getImages(),","));//取多张图片的第一张sku.getImages().split(",")[0]
            skus.add(map);

            //把价格放到set中
            priceSet.add(sku.getPrice());
        }
        //Set<Long> priceSet = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        //规格参数
        /**
         * key在规格参数表，value在商品详情表
         * 一个分类对应一套规格参数
         */
        //查询规格参数
        List<SpecParam> mapkeys = specificationClient.queryParamList(null, spu.getCid3(), true);
        if(CollectionUtils.isEmpty(mapkeys)){
            throw new CmException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }

        //查询商品详情(包含通用规格参数 特有规格参数) 商品详情里面存的规格参数是json 格式是:规格参数id:规格参数值
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);

        //获取商品详情的通用规格参数
        String genericSpecJson = spuDetail.getGenericSpec();
        Map<Long, String> genericSpec = JsonUtils.parseMap(genericSpecJson, Long.class, String.class);

        //获取商品详情的特殊规格参数
        String specialSpecJosn = spuDetail.getSpecialSpec();
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(specialSpecJosn, new TypeReference<Map<Long, List<String>>>() {
        });

        //规格参数的key是规格参数的名字，值是规格参数的值
        Map<String,Object> specs=new HashMap<>();
        for (SpecParam mapkey : mapkeys) {
            //规格名称
            String key = mapkey.getName();
            Object value="";
            //判断是否是通用规格参数
            if(mapkey.getGeneric()){
                value=genericSpec.get(mapkey.getId());
                //判断是否是数值类型
                if(mapkey.getNumeric()){
                    //处理成段，比如5.5英寸就往索引库里存5.0-5.5 不然搜索的时候就来麻烦了
                   value= chooseSegment(value.toString(),mapkey);//value是5.5 mapkey是规格参数 mapkey.getSegment是5.0-5.5
                }
            }else{
                //特有规格参数是个集合不需要处理
                value=specialSpec.get(mapkey.getId());
            }
        //存入map
            specs.put(key,value);
        }

        //构建goods对象
        Goods goods=new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid3(spu.getCid3());

        goods.setCreateTime(spu.getCreateTime());
        //goods的id就是spu的id
        goods.setId(spuId);
        goods.setAll(all);// 搜索字段，包含标题、分类、品牌、规格
        goods.setPrice(priceSet);// 所有sku的价格集合
        goods.setSkus(JsonUtils.serialize(skuList)); // 所有sku的集合的json结构,也可以在页面查询都一样
        goods.setSpecs(specs); // 所有可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        int page= request.getPage()-1;//elasticsearch默认页是从0开始
        int size= request.getSize();
        //1.创建查询构建器
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //2.结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //3.分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //4.查询条件
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", request.getKey());
        QueryBuilder basicQuery = buildBasicQuery(request);

        queryBuilder.withQuery(basicQuery);
        //4.1聚合分类和品牌信息
        //聚合分类  给聚合起名字
        String CategoryAggName="category_agg"; //对cid3进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(CategoryAggName).field("cid3"));
        //聚合品牌
        String BrandAggName="brand_agg"; //对cid3进行聚合
        queryBuilder.addAggregation(AggregationBuilders.terms(BrandAggName).field("brandId"));
        //5.查询 为了返回聚合结果，不能用Goods来接收了
        //Page<Goods> result = repositiory.search(queryBuilder.build());
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //6.解析结果
        //6.1解析分页结果
        long totalElements = result.getTotalElements();//总条数
        long totalPages = result.getTotalPages();//总页数
        List<Goods> goodsList = result.getContent();
        /*System.out.println(goodsList);*/
        //6.2解析聚合结果
        Aggregations aggs = result.getAggregations();//拿到两个聚合
        List<Category> categories=parseCategoryAgg(aggs.get(CategoryAggName));

        List<Brand> brands=parseBrandAgg(aggs.get(BrandAggName));

        /**  在页面上展示，其实我们只需要id subtitle skus
         * GET /goods/_search
         * {
         * "query":{
         *      "match":{
         *      "all":"手机"
         * }},
         * "_source":["id","subTitle","skus"]
         * }
         **/
        //7对规格参数聚合
        List<Map<String,Object>> specs=null;

        if(categories!=null && categories.size()==1){
            // 只有当商品分类是1一个的时候，才去对商品规格参数进行聚合
            specs=buildSpecificationAgg(categories.get(0).getId(),basicQuery);//传递商品分类 和基础查询条件，在原来查询条件的基础上
        }
        //return new PageResult<Goods>(totalElements,totalPages,goodsList);
        //SearchResult继承了Pageresult 并且多返回了categories和brands
        return new SearchResult(totalElements,totalPages,goodsList,categories,brands,specs);
    }

    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //创建布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",request.getKey()));
        //过滤条件 将分类，品牌(long类型)，规格参数这些填进去
        if(request.getFilter()!=null) {
            Map<String, String> filter = request.getFilter();
            for (Map.Entry<String, String> entry : filter.entrySet()) {
                String key = entry.getKey();
                //处理key
                if (!"cid3".equals(key) && !"brandId".equals(key)) {//是分类或者品牌
                /*我们存索引的时候是 specs:{"cpu核数":"32核",
                                            "后置摄像头":"25000万以上",
                                            "cpu品牌":"麒麟"，
                                            "cpu频率":"2.5GHz以上"}
                */
                    key = "specs." + key + ".keyword";
                }
                String value = entry.getValue();
                boolQueryBuilder.filter(QueryBuilders.termQuery(key, value));
            }
        }
        return boolQueryBuilder;
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String,Object>> specs=new ArrayList<>();
        //1.把查询出的结果的分类下所有可查询的规格参数查出来
        List<SpecParam> specParams = specificationClient.queryParamList(null, cid, true);
        //2.聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2.1在原来查询结果的基础上进行聚合，带上查询条件
        queryBuilder.withQuery(basicQuery);
        //2.2聚合
        for (SpecParam specParam : specParams) {
            String specParamName = specParam.getName();
            //"filed":"specs.内存.keyword"
            //因为存储的是keyword 所以可以不用进行分词聚合  field()聚合字段
            queryBuilder.addAggregation(
                    AggregationBuilders.terms(specParamName).field("specs."+specParamName+".keyword"));
        }
        //3.获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //4.解析结果
        Aggregations aggregations = result.getAggregations();
        //通过规格参数的名字拿到聚合结果
        for (SpecParam specParam : specParams) {
            //规格参数名
            String specParamName = specParam.getName();
            StringTerms terms = aggregations.get(specParamName);
            /**
             * 获取buckets
             * "buckets":[
             * {"key":"4GB",
             * "doc_count":76},
             * {"key":"6GB",
             * "doc_count":49}
             * ]
             **/
            //把聚合后返回的 结果key-doc_count结构转成key的List结构
            List<String> options = terms.getBuckets()
                        .stream().map(b -> b.getKeyAsString()).collect(Collectors.toList());
            //准备map
            Map<String,Object> map=new HashMap<>();
            map.put("k",specParamName);
            map.put("options",options);
            specs.add(map);

        }
        return specs;
    }

    private List<Brand> parseBrandAgg(Aggregation aggregation) {
        try {
            LongTerms terms = (LongTerms) aggregation;
            List<Long> brandIds = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(brandIds);
            return brands;
        }catch (Exception e){
            //如果没查到就抛异常，没有聚合到任何品牌，接收到前面抛出的空异常，返回空
            log.error("搜索服务查询品牌异常",e);
            return null;
        }
    }

    //Aggregation里面有好多term
    private List<Category> parseCategoryAgg(Aggregation aggregation) {
        try {
            LongTerms terms = (LongTerms) aggregation;
            List<Long> categoryIds = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryByIds(categoryIds);
            return categories;
        }catch (Exception e){
            //如果没查到就抛异常，没有聚合到任何品牌，接收到前面抛出的空异常，返回空
            log.error("搜索服务查询分类异常",e);
            return null;
        }
    }

    //
    public void createOrUpdateIndex(Long spuId) {
        //不要捕获异常，有异常会抛出，spring开启了手动ack，这样ack就不会运行 消息会回滚
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods对象
        Goods goods = buildGoods(spu);
        //存入索引库
        repositiory.save(goods);
    }

    public void deleteIndex(Long spuId) {
        repositiory.deleteById(spuId);
    }
}
