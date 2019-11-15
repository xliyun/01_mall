package com.cloudmall.item.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.common.vo.PageResult;
import com.cloudmall.item.mapper.*;
import com.cloudmall.item.pojo.*;
import com.cloudmall.item.vo.SpuVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private Spu2Mapper spu2Mapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuVo> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索字段过滤
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");

        }
        //上下架过滤
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序 商品更新时间
        example.setOrderByClause("last_update_time DESC");

        List<Spu> spus = spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(spus)){
            throw new CmException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //解析分类和品牌的名称 多加了分类名称和品牌名称
        List<SpuVo> spuVos=loadCategoryAndBrandName(spus);
        //解析分页结果
        PageInfo<Spu> info = new PageInfo<>();


        return new PageResult<SpuVo>(info.getTotal(),spuVos);
    }

    private List<SpuVo> loadCategoryAndBrandName(List<Spu> spus) {
        List<SpuVo> spuVos=new ArrayList<>();
        for (Spu spu : spus) {
            //处理分类名称 分类1/分类2/分类3
            List<String> collect = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream().map(Category::getName).collect(Collectors.toList());
            SpuVo spuVo = new SpuVo();
            spuVo.setBrandId(spu.getBrandId());
            spuVo.setId(spu.getId());
            spuVo.setSubTitle(spu.getSubTitle());
            spuVo.setSaleable(spu.getSaleable());
            spuVo.setTitle(spu.getTitle());
            spuVo.setCid1(spu.getCid1());
            spuVo.setCid2(spu.getCid2());
            spuVo.setCid3(spu.getCid3());

            spuVo.setCname(StringUtils.join(collect,"/"));
            //处理品牌名称
            spuVo.setBname(brandService.queryById(spu.getBrandId()).getName());
            spuVos.add(spuVo);
        }
        return spuVos;
    }

    @Transactional
    public void saveGoods(Spu2 spu2) {
        //新增spu
        spu2.setId(null);
        spu2.setCreateTime(new Date());
        spu2.setLastUpdateTime(spu2.getCreateTime());
        spu2.setSaleable(true);
        spu2.setValid(false);
        int ispu2 = spu2Mapper.insert(spu2);
        if(ispu2!=1){
            throw new CmException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
        //新增spu的detail
        SpuDetail spuDetail = spu2.getSpuDetail();
        spuDetail.setSpuId(spu2.getId());
        spuDetailMapper.insert(spuDetail);

        //ctrl+alt+m把这段逻辑给重构了
        //新增sku和库存
        saveSkuAndStock(spu2);

        //防止发送消息失败影响商品保存代码
        try {
            //TODO
            //增删改发送mq消息 把spu的id发送出去 搜索服务和静态页服务接收
            amqpTemplate.convertAndSend("item.insert",spu2.getId());
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    private void saveSkuAndStock(Spu2 spu2) {
        //定义库存集合
        List<Stock> stockList=new ArrayList<>();
        //新增sku
        List<Sku> skus = spu2.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu2.getId());
            int isku = skuMapper.insert(sku);
            if(isku!=1){
                throw new CmException(ExceptionEnum.GOODS_SAVE_ERROR);
            }
            //新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);

        }
        //批量新增的id字段名必须是id并且是自增列
        int istock = stockMapper.insertList(stockList);
        if(istock!=1){
            throw new CmException(ExceptionEnum.GOODS_SAVE_ERROR);
        }
    }

    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail spuDetail = spuDetailMapper.selectByPrimaryKey(spuId);
        if(spuDetail==null){
            throw new CmException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
        return spuDetail;
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku=new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if(CollectionUtils.isEmpty(skuList)){
            throw new CmException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
/*        //查询库存
        for (Sku s : skuList) {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            if(stock==null){
                throw new CmException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
            }
            s.setStock(stock.getStock());
        }*/
        //将sku的id都放到map里
        loadStockInSku(skuList);
        return skuList;
    }

    @Transactional
    public void updateGoods(Spu2 spu2) {
        if(spu2.getId()==null){
            throw new CmException(ExceptionEnum.GOODS_ID_CANNOT_BE_NULL);
        }
        Sku sku=new Sku();
        sku.setSpuId(spu2.getId());
        //查询sku
        List<Sku> skuList = skuMapper.select(sku);
        if(!CollectionUtils.isEmpty(skuList)){
            //删除sku
            int dsku = skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
            stockMapper.deleteByIdList(ids);
        }

        //修改spu
        spu2.setValid(null);
        spu2.setSaleable(null);
        spu2.setLastUpdateTime(new Date());
        spu2.setCreateTime(new Date());
        //会对字段进行判断再更新(如果为Null就忽略更新)
        int ispu2 = spu2Mapper.updateByPrimaryKeySelective(spu2);
        if(ispu2!=1){
            throw new CmException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改detail
        int idetail = spuDetailMapper.updateByPrimaryKeySelective(spu2.getSpuDetail());
        if(idetail!=1){
            throw new CmException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //新增sku和stock
        saveSkuAndStock(spu2);

        //防止发送消息失败影响商品保存代码
        try {
            //发送mq消息 把spu的id发送出去 搜索服务和静态页服务接收
            amqpTemplate.convertAndSend("item.update",spu2.getId());
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    public Spu querySpuById(Long spuId) {
        //查询spu
        Spu spu = spuMapper.selectByPrimaryKey(spuId);
        if(spu==null){
            throw new CmException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //查询sku
        List<Sku> skuList = querySkuBySpuId(spuId);
        spu.setSkus(skuList);
        //查询detail

        spu.setSpuDetail(queryDetailById(spuId));
        return spu;
    }

    public List<Sku> querySkuByIds(List<Long> ids) {
        List<Sku> skuList = skuMapper.selectByIdList(ids);
        if(CollectionUtils.isEmpty(skuList)){
            throw  new CmException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }


        loadStockInSku(skuList);

        return skuList;
    }

    //查询库存
    private void loadStockInSku(List<Sku> skuList) {
        List<Long> skuids = skuList.stream().map(Sku::getId).collect(Collectors.toList());
        //一次查出所有的库存
        List<Stock> stockList = stockMapper.selectByIdList(skuids);
        if (CollectionUtils.isEmpty(stockList)) {
            throw new CmException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
        }

        //我们把stock变成一个map，其key是sku的id 值是库存值
        Map<Long, Integer> stockMap = stockList.stream().collect(Collectors.toMap(Stock::getSkuId, Stock::getStock));
        skuList.forEach(s -> s.setStock(stockMap.get(s.getId())));
    }
}
