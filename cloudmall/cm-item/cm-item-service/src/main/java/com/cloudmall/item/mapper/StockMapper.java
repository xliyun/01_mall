package com.cloudmall.item.mapper;

import com.cloudmall.common.mapper.BaseListMapper;
import com.cloudmall.item.pojo.Stock;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.BaseMapper;
import tk.mybatis.mapper.common.Mapper;
//BaseMapper源码里面包含增删改查的各种mapper,我们仿照着把InsertListMapper在common里写一个
//这里是允许自己定义id的listMapper
public interface StockMapper extends Mapper<Stock>, IdListMapper<Stock,Long>,InsertListMapper<Stock> {
}
