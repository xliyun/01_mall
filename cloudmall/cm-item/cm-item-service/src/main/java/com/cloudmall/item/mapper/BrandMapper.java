package com.cloudmall.item.mapper;

import com.cloudmall.common.mapper.BaseListMapper;
import com.cloudmall.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends BaseListMapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id, brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("select b.* from tb_category_brand cb INNER JOIN tb_brand b ON b.id = cb.brand_id WHERE cb.category_id=#{cid}")
    List<Brand> queryByCategoryId(@Param("cid")Long cid);
}
