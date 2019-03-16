package com.cloudmall.item.service;

import com.cloudmall.common.enums.ExceptionEnum;
import com.cloudmall.common.exception.CmException;
import com.cloudmall.common.vo.PageResult;
import com.cloudmall.item.mapper.BrandMapper;
import com.cloudmall.item.pojo.Brand;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sun.swing.StringUIClientPropertyKey;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Collection;
import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        //分页
        //原理:利用mybatis的拦截器，对要执行的sql进行拦截，自动在后面拼上limit
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);
        System.out.println(key);
        if(StringUtils.isNotBlank(key)){
            //过滤条件
            example.createCriteria().orLike("name","%"+key+"%").orEqualTo("letter",key.toUpperCase());
        }
        //排序
        if(StringUtils.isNotBlank(sortBy)){
            String orderByClause=sortBy+(desc?" DESC":" ASC");
            example.setOrderByClause(orderByClause);
        }

        //查询
        List<Brand> brands = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(brands)){
            throw new CmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //解析分页结果
         PageInfo<Brand> info=new PageInfo<>(brands);
        return new PageResult<>(info.getTotal(),brands);
    }
    //加上事务
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //insert和insertSelective
        //insertSelective选择性新增，只新增非空字段
        //count是1就是成功了
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if(count!=1){
            throw new CmException(ExceptionEnum.BRAND_SAVE_EROOR);
        }
        //新增中间表
        for (Long cid : cids) {
            //brand会自动回显
            int count2=brandMapper.insertCategoryBrand(cid,brand.getId());
            if(count2!=1){
                throw new CmException(ExceptionEnum.BRAND_INTERMEDIDATE_SAVE_EROOR);
            }
        }
    }

    public Brand queryById(Long id){
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand==null){
            throw new CmException(ExceptionEnum.BRAND_SAVE_EROOR);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> brands = brandMapper.queryByCategoryId(cid);
        if(CollectionUtils.isEmpty(brands)){
            throw new CmException(ExceptionEnum.BRAND_SAVE_EROOR);
        }
        return brands;
    }
}
